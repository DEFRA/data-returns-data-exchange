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

/**********************************************************************

 Rules for the dependency table and cache

 **********************************************************************

 These symbols are used in the dependencies table

 *      - Any item - must supply (Not terminating)
 ^Item  - disallowed item (Terminating)
 Item   - allowed item (Not terminating)
 ^*     - No item or error (Not terminating)
 *-     - Any item but optionally not supplied (Not terminating)
 ...    - Irrelevant (Terminating)

 Example 1 - must be either Item1,Item2,Item3

 Item1
 Item2
 Item3

 Example 2 - must be either Item1, Item2, Item3 but optionally not supplied

 Item1
 Item2
 Item3
 *-

 Example 3 - must not be supplied otherwise error
 ^*

 Example 4 - must be either Item1, Item2, Item3 but not item4
 Item1
 Item2
 Item3
 ^Item4,...,...

 Example 5 - Any Item
 *

 Example 6 - Any item except Item4
 *
 ^Item4,...,...

 Example 7 Any item except Item4 but optionally not supplied
 ^Item4
 *-

 **************************************************************/
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

    public enum DependencyValidationHierarchy {
        RETURN_TYPE(0), RELEASE(1), PARAMETER(2), UNIT(3);

        private int level = 0;
        private DependencyValidationHierarchy[] values;

        DependencyValidationHierarchy(int level) {
            this.level = level;
        }

        public DependencyValidationHierarchy[] getValues() {
            return DependencyValidationHierarchy.values();
        }

        public DependencyValidationHierarchy next() {
            DependencyValidationHierarchy[] arr = getValues();
            return arr[this.level + 1];
        }
    }

    public enum DependencyValidationResultType {
        OK, EXPECTED, NOT_EXPECTED, NOT_FOUND, EXCLUDED
    }

    public Pair<DependencyValidationHierarchy, DependencyValidationResultType> validate(ReturnType returnType,
                                               ReleasesAndTransfers releasesAndTransfers,
                                               Parameter parameter,
                                               Unit unit) {

        // Get key data
        String returnTypeName = returnType == null ? null : returnTypeDao.getKeyFromRelaxedName(returnType.getName());
        String releasesAndTransfersName = releasesAndTransfers == null ? null : releasesAndTransfersDao.getKeyFromRelaxedName(releasesAndTransfers.getName());
        String parameterName = parameter == null ? null : parameterDao.getKeyFromRelaxedName(parameter.getName());
        String unitName = unit == null ? null : unitDao.getKeyFromRelaxedName(unit.getName());

        return evaluate(DependencyValidationHierarchy.RETURN_TYPE, (Map)dao.getCache(),
                new String[]{returnTypeName, releasesAndTransfersName, parameterName, unitName}
        );

    }

    /*
     * These overrides deal with varying input
     */
    public Pair<DependencyValidationHierarchy, DependencyValidationResultType> validate(ReturnType returnType, Parameter parameter, Unit unit) {
        return validate(returnType, null, parameter, unit);
    }

    public Pair<DependencyValidationHierarchy, DependencyValidationResultType> validate(ReturnType returnType, Parameter parameter) {
         return validate(returnType, null, parameter, null);
    }

    /*
     * Helper function to direct to the map or set evaluator - the hierarchy is terminated by a set
     */
    private Pair<DependencyValidationHierarchy, DependencyValidationResultType> getDependencyValidationResult(DependencyValidationHierarchy level, Map cache, String cacheKey, String[] entityName) {
        if (cache.get(cacheKey) instanceof Set) {
            return evaluate(level.next(), (Set)cache.get(cacheKey), Arrays.copyOfRange(entityName, 1, entityName.length));
        } else {
            return evaluate(level.next(), (Map)cache.get(cacheKey), Arrays.copyOfRange(entityName, 1, entityName.length));
        }
    }

    private Pair<DependencyValidationHierarchy, DependencyValidationResultType> evaluate(DependencyValidationHierarchy level, Map cache, String... entityName) {
        if (entityName[0] != null) {
            /*
             * If the entity name is supplied (not null)
             */
            if (cache.containsKey("^" + entityName[0])) {
                // If we have supplied an explicitly excluded item then report an error
                return Pair.of(level, DependencyValidationResultType.EXCLUDED);
            } else if (cache.containsKey("^*")) {
                // If we have the inverse wildcard we are not expecting an item so error
                return Pair.of(level, DependencyValidationResultType.NOT_EXPECTED);
            } else if (cache.containsKey(entityName[0])) {
                // Item explicitly listed - Proceed
                return getDependencyValidationResult(level, cache, entityName[0], entityName);
            } else if(cache.containsKey("*-")) {
                // if the item is optionally supplied with a wildcard - proceed
                return getDependencyValidationResult(level, cache, "*-", entityName);
            } else if (cache.containsKey("*")) {
                // if the item is on a wildcard - proceed
                return getDependencyValidationResult(level, cache, "*", entityName);
            } else if (cache.containsKey("...")) {
                // We don't care - OK
                return Pair.of(level, DependencyValidationResultType.OK);
            } else {
                // We didn't find what we were looking for
                return Pair.of(level, DependencyValidationResultType.NOT_FOUND);
            }
        } else {
            /*
             * If the entity name is not supplied (null)
             */
            if (cache.containsKey("^*")) {
                // If we have the inverse wildcard we are not expecting an item so no error - proceed
                return getDependencyValidationResult(level, cache, "^*", entityName);
            } else if(cache.containsKey("*-")) {
                // if the item is optionally supplied with a wildcard we are good - proceed
                return getDependencyValidationResult(level, cache, "*-", entityName);
            } else if (cache.containsKey("*")) {
                // if the item is on a wildcard its an error
                return Pair.of(level, DependencyValidationResultType.EXPECTED);
            } else if (cache.containsKey("...")) {
                // We don't care - OK
                return Pair.of(level, DependencyValidationResultType.OK);
            } else {
                // This is wrong - nothing is given but we are expecting something
                return Pair.of(level, DependencyValidationResultType.EXPECTED);
            }
        }
    }

    /*
     * Operation is terminated with a set lookup
     */
    private Pair<DependencyValidationHierarchy, DependencyValidationResultType> evaluate(DependencyValidationHierarchy level, Set cache, String... entityName) {
        if (entityName[0] != null) {
            /*
             * If the entity name is supplied (not null)
             */
            if (cache.contains("^" + entityName[0])) {
                // If we have supplied an explicitly excluded item then report an error
                return Pair.of(level, DependencyValidationResultType.EXCLUDED);
            } else if (cache.contains("^*")) {
                // If we have the inverse wildcard we are not expecting an item so error
                return Pair.of(level, DependencyValidationResultType.NOT_EXPECTED);
            } else if (cache.contains(entityName[0])) {
                // Item explicitly listed - Proceed
                return Pair.of(level, DependencyValidationResultType.OK);
            } else if(cache.contains("*-")) {
                // if the item is optionally supplied with a wildcard - OK
                return Pair.of(level, DependencyValidationResultType.OK);
            } else if (cache.contains("*")) {
                // if the item is on a wildcard - OK
                return Pair.of(level, DependencyValidationResultType.OK);
            } else if (cache.contains("...")) {
                // We don't care - OK
                return Pair.of(level, DependencyValidationResultType.OK);
            } else {
                // We have not found the item
                return Pair.of(level, DependencyValidationResultType.NOT_FOUND);
            }
        } else {
            /*
             * If the entity name is not supplied (null)
             */
            if (cache.contains("^*")) {
                // If we have the inverse wildcard we are not expecting an item so no error - ok
                return Pair.of(level, DependencyValidationResultType.OK);
            } else if(cache.contains("*-")) {
                // if the item is optionally supplied with a wildcard we are good
                return Pair.of(level, DependencyValidationResultType.OK);
            } else if (cache.contains("*")) {
                // if the item is on a plain wildcard its an error
                return Pair.of(level, DependencyValidationResultType.EXPECTED);
            } else if (cache.contains("...")) {
                // We don't care - OK
                return Pair.of(level, DependencyValidationResultType.OK);
            } else {
                // This is wrong - nothing is given but we are expecting something.
                return Pair.of(level, DependencyValidationResultType.EXPECTED);
            }
        }
    }
}
