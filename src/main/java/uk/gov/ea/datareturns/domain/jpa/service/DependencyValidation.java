package uk.gov.ea.datareturns.domain.jpa.service;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import uk.gov.ea.datareturns.domain.jpa.dao.*;
import uk.gov.ea.datareturns.domain.jpa.entities.*;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Validate a given input against the set of mutual dependencies between lists
 * Return Type -> Releases and transfers (For PI) -> Parameters -> Units
 * The dependencies are encoded in the in Dependencies.csv file
 */
@Service
public class DependencyValidation {

    private ParameterDao parameterDao;
    private ReturnTypeDao returnTypeDao;
    private ReleasesAndTransfersDao releasesAndTransfersDao;
    private UnitDao unitDao;
    private DependenciesDao dao;

    @Inject
    public DependencyValidation(ParameterDao parameterDao, ReturnTypeDao returnTypeDao,
                                ReleasesAndTransfersDao releasesAndTransfersDao,
                                UnitDao unitDao, DependenciesDao dao) {
        this.parameterDao = parameterDao;
        this.returnTypeDao = returnTypeDao;
        this.releasesAndTransfersDao = releasesAndTransfersDao;
        this.unitDao = unitDao;
        this.dao = dao;
    }

    /*
     * The result of the validation. Excluded entities are distinguished from
     * not found. From an end user perspective the two things are the same.
     * EXPECTED indicates that a required entity has not been supplied and
     * NOT_EXPECTED indicates that an entity which is explicitly not required was supplied
     */
    public enum Result {
        OK, EXPECTED, NOT_EXPECTED, NOT_FOUND, EXCLUDED
    }

    /*
     * The top level call into the dependency validator returning the result
     * as a pair of the validating entity and the result. The validating entity is
     * on relevant where the result is not OK, otherwise it holds the lowest level validated
     */
    public Pair<ControlledListsList, Result> validate(ReturnType returnType,
                                                      ReleasesAndTransfers releasesAndTransfers,
                                                      Parameter parameter,
                                                      Unit unit) {

        /*
         * All the caches use the normalized keys. Alias resolution will happen
         * prior when instantiating the entities
         */
        String returnTypeName = returnType == null ? null : returnTypeDao.getKeyFromRelaxedName(returnType.getName());
        String releasesAndTransfersName = releasesAndTransfers == null ? null : releasesAndTransfersDao.getKeyFromRelaxedName(releasesAndTransfers.getName());
        String parameterName = parameter == null ? null : parameterDao.getKeyFromRelaxedName(parameter.getName());
        String unitName = unit == null ? null : unitDao.getKeyFromRelaxedName(unit.getName());

        return evaluate(ControlledListsList.RETURN_TYPE, (Map)dao.getCache(),
                returnTypeName, releasesAndTransfersName, parameterName, unitName);
    }

    /*
     * These other signatures are for convenience
     */
    public Pair<ControlledListsList, Result> validate(ReturnType returnType, Parameter parameter, Unit unit) {
        return validate(returnType, null, parameter, unit);
    }

    public Pair<ControlledListsList, Result> validate(ReturnType returnType, Parameter parameter) {
         return validate(returnType, null, parameter, null);
    }

    /*
     * Helper function to direct to the map or set evaluator - the hierarchy is terminated by a set
     * So the system has been set up so that initial cache is
     * Map<String, Map<String, Map<String, Set<String>>>>
     * Then as each entity is validated the cache is drilled into so that we get the following
     * sequence
     *
     * Map<String, Map<String, Map<String, Set<String>>>> - Cache by Return type
     * Map<String, Map<String, Set<String>>> - cache by releases and transfers
     * Map<String, Set<String>> - cache by parameters
     * Set<String> - a hash-set of units
     */
    private Pair<ControlledListsList, Result> getDependencyValidationResult(ControlledListsList level, Map cache, String cacheKey, String[] entityName) {
        if (cache.get(cacheKey) instanceof Set) {
            return evaluate(level.next(), (Set)cache.get(cacheKey), Arrays.copyOfRange(entityName, 1, entityName.length));
        } else {
            return evaluate(level.next(), (Map)cache.get(cacheKey), Arrays.copyOfRange(entityName, 1, entityName.length));
        }
    }

    /*
     * Main evaluating function which is recursive as the rules are the same for each entity
     */
    protected Pair<ControlledListsList, Result> evaluate(ControlledListsList level, Map cache, String... entityName) {
        if (entityName[0] != null) {
            /*
             * If the entity name is supplied (not null)
             */
            if (cache.containsKey(DependencyValidationSymbols.EXCLUDE + entityName[0])) {
                // If we have supplied an explicitly excluded item then report an error
                return Pair.of(level, Result.EXCLUDED);
            } else if (cache.containsKey(DependencyValidationSymbols.EXCLUDE_ALL)) {
                // If we have the inverse wildcard we are not expecting an item so error
                return Pair.of(level, Result.NOT_EXPECTED);
            } else if (cache.containsKey(entityName[0])) {
                // Item explicitly listed - Proceed
                return getDependencyValidationResult(level, cache, entityName[0], entityName);
            } else if(cache.containsKey(DependencyValidationSymbols.INCLUDE_ALL_OPTIONALLY)) {
                // if the item is optionally supplied with a wildcard - proceed
                return getDependencyValidationResult(level, cache, DependencyValidationSymbols.INCLUDE_ALL_OPTIONALLY, entityName);
            } else if (cache.containsKey(DependencyValidationSymbols.INCLUDE_ALL)) {
                // if the item is on a wildcard - proceed
                return getDependencyValidationResult(level, cache, DependencyValidationSymbols.INCLUDE_ALL, entityName);
            } else if (cache.containsKey(DependencyValidationSymbols.NOT_APPLICABLE)) {
                // We don't care - OK
                return Pair.of(level, Result.OK);
            } else {
                // We didn't find what we were looking for
                return Pair.of(level, Result.NOT_FOUND);
            }
        } else {
            /*
             * If the entity name is not supplied (null)
             */
            if (cache.containsKey(DependencyValidationSymbols.EXCLUDE_ALL)) {
                // If we have the inverse wildcard we are not expecting an item so no error - proceed
                return getDependencyValidationResult(level, cache, DependencyValidationSymbols.EXCLUDE_ALL, entityName);
            } else if(cache.containsKey(DependencyValidationSymbols.INCLUDE_ALL_OPTIONALLY)) {
                // if the item is optionally supplied with a wildcard we are good - proceed
                return getDependencyValidationResult(level, cache, DependencyValidationSymbols.INCLUDE_ALL_OPTIONALLY, entityName);
            } else if (cache.containsKey(DependencyValidationSymbols.INCLUDE_ALL)) {
                // if the item is on a wildcard its an error
                return Pair.of(level, Result.EXPECTED);
            } else if (cache.containsKey(DependencyValidationSymbols.NOT_APPLICABLE)) {
                // We don't care - OK
                return Pair.of(level, Result.OK);
            } else {
                // This is wrong - nothing is given but we are expecting something
                return Pair.of(level, Result.EXPECTED);
            }
        }
    }

    /*
     * If the validation is not already complete and returned out of the above
     * recursive method it is terminated by this operation with a set lookup
     */
    private Pair<ControlledListsList, Result> evaluate(ControlledListsList level, Set cache, String... entityName) {
        if (entityName[0] != null) {
            /*
             * If the entity name is supplied (not null)
             */
            if (cache.contains(DependencyValidationSymbols.EXCLUDE + entityName[0])) {
                // If we have supplied an explicitly excluded item then report an error
                return Pair.of(level, Result.EXCLUDED);
            } else if (cache.contains(DependencyValidationSymbols.EXCLUDE_ALL)) {
                // If we have the inverse wildcard we are not expecting an item so error
                return Pair.of(level, Result.NOT_EXPECTED);
            } else if (cache.contains(entityName[0])) {
                // Item explicitly listed - Proceed
                return Pair.of(level, Result.OK);
            } else if(cache.contains(DependencyValidationSymbols.INCLUDE_ALL_OPTIONALLY)) {
                // if the item is optionally supplied with a wildcard - OK
                return Pair.of(level, Result.OK);
            } else if (cache.contains(DependencyValidationSymbols.INCLUDE_ALL)) {
                // if the item is on a wildcard - OK
                return Pair.of(level, Result.OK);
            } else if (cache.contains(DependencyValidationSymbols.NOT_APPLICABLE)) {
                // We don't care - OK
                return Pair.of(level, Result.OK);
            } else {
                // We have not found the item
                return Pair.of(level, Result.NOT_FOUND);
            }
        } else {
            /*
             * If the entity name is not supplied (null)
             */
            if (cache.contains(DependencyValidationSymbols.EXCLUDE_ALL)) {
                // If we have the inverse wildcard we are not expecting an item so no error - ok
                return Pair.of(level, Result.OK);
            } else if(cache.contains(DependencyValidationSymbols.INCLUDE_ALL_OPTIONALLY)) {
                // if the item is optionally supplied with a wildcard we are good
                return Pair.of(level, Result.OK);
            } else if (cache.contains(DependencyValidationSymbols.INCLUDE_ALL)) {
                // if the item is on a plain wildcard its an error
                return Pair.of(level, Result.EXPECTED);
            } else if (cache.contains(DependencyValidationSymbols.NOT_APPLICABLE)) {
                // We don't care - OK
                return Pair.of(level, Result.OK);
            } else {
                // This is wrong - nothing is given but we are expecting something.
                return Pair.of(level, Result.EXPECTED);
            }
        }
    }
}
