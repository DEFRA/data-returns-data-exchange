package uk.gov.ea.datareturns.domain.jpa.service;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import uk.gov.ea.datareturns.domain.jpa.dao.*;
import uk.gov.ea.datareturns.domain.jpa.entities.Parameter;
import uk.gov.ea.datareturns.domain.jpa.entities.ReleasesAndTransfers;
import uk.gov.ea.datareturns.domain.jpa.entities.ReturnType;
import uk.gov.ea.datareturns.domain.jpa.entities.Unit;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Evaluate if the required mutual dependencies between lists are met
 * by the input data
 */
@Service
public class DependencyValidation {

    @Inject
    private ParameterDao parameterDao;

    @Inject
    private ReturnTypeDao returnTypeDao;

    @Inject
    private ReleasesAndTransfersDao releasesAndTransfersDao;

    @Inject
    private UnitDao unitDao;

    @Inject
    DependenciesDao dao;

    /*
     * The set of mutually dependent entities which are being validated. The level specifies
     * the order of validation starting at RETURN_TYPE. Where the result is OK the entity is irrelevant
     */
    public enum Entity {
        RETURN_TYPE(0), RELEASE(1), PARAMETER(2), UNIT(3);

        private int level = 0;
        private Entity[] values;

        Entity(int level) {
            this.level = level;
        }

        public Entity[] getValues() {
            return Entity.values();
        }

        public Entity next() {
            Entity[] arr = getValues();
            return arr[this.level + 1];
        }
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
    public Pair<Entity, Result> validate(ReturnType returnType,
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

        return evaluate(Entity.RETURN_TYPE, (Map)dao.getCache(),
                new String[]{returnTypeName, releasesAndTransfersName, parameterName, unitName}
        );

    }

    /*
     * These overrides are for convenience
     */
    public Pair<Entity, Result> validate(ReturnType returnType, Parameter parameter, Unit unit) {
        return validate(returnType, null, parameter, unit);
    }

    public Pair<Entity, Result> validate(ReturnType returnType, Parameter parameter) {
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
    private Pair<Entity, Result> getDependencyValidationResult(Entity level, Map cache, String cacheKey, String[] entityName) {
        if (cache.get(cacheKey) instanceof Set) {
            return evaluate(level.next(), (Set)cache.get(cacheKey), Arrays.copyOfRange(entityName, 1, entityName.length));
        } else {
            return evaluate(level.next(), (Map)cache.get(cacheKey), Arrays.copyOfRange(entityName, 1, entityName.length));
        }
    }

    /*
     * Main evaluating function which is recursive as the rules are the same for each entity
     */
    private Pair<Entity, Result> evaluate(Entity level, Map cache, String... entityName) {
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
    private Pair<Entity, Result> evaluate(Entity level, Set cache, String... entityName) {
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
