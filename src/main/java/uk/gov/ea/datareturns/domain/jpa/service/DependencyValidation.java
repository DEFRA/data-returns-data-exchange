package uk.gov.ea.datareturns.domain.jpa.service;

import org.springframework.stereotype.Service;
import uk.gov.ea.datareturns.domain.jpa.dao.*;
import uk.gov.ea.datareturns.domain.jpa.entities.Parameter;
import uk.gov.ea.datareturns.domain.jpa.entities.ReleasesAndTransfers;
import uk.gov.ea.datareturns.domain.jpa.entities.ReturnType;
import uk.gov.ea.datareturns.domain.jpa.entities.Unit;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

/**
 * Created by graham on 04/10/16.
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

    public enum DependencyValidationResult {
        BAD_RETURN_TYPE, BAD_RELEASE, NO_PARAMETER, OK, NO_RELEASE,
        BAD_PARAMETER, BAD_UNITS, BAD_DATA, RELEASE_NOT_APPLICABLE,
        UNIT_NOT_APPLICABLE, NO_RETURN_TYPE
    }

    /*
     * Validation starts by validating the return type and releases and transfers
     *
     * The validation rules are as follows
     * (1) A return type must be specified in all circumstances otherwise NO_RETURN_TYPE is returned
     * (2) If the return type is one for which releases and transfers are specified
     *     then the releases and transfers are either stated explicitly in the dependencies file or
     *     may be declared as the wildcard '*' which allows any set value. In the first case if
     *     the release is not matched then BAD_RELEASE is returned. With the wildcard then any release
     *     is allowed (it is assumed that the entity is already atomically checked in the list)
     * (3) Parameters are always required. In the dependencies table they may me listed explicitly
     *     or with a wildcard '*' or disallowed with the inverse wildcard '^*'. Either BAD_PARAMETER
     *     or NO_PARAMETER can be returned.
     * (4) The units are not required unless specified by the inverse wildcard '^*' in which case
     *     UNIT_NOT_APPLICABLE is returned. If a unit is supplied and not found then BAD_UNITS
     *     is returned.
     */
    public DependencyValidationResult validate(ReturnType returnType,
                                               ReleasesAndTransfers releasesAndTransfers,
                                               Parameter parameter,
                                               Unit unit) {

        // Get key data
        String returnTypeName = returnType == null ? null : returnTypeDao.getKeyFromRelaxedName(returnType.getName());
        String releasesAndTransfersName = releasesAndTransfers == null ? null : releasesAndTransfersDao.getKeyFromRelaxedName(releasesAndTransfers.getName());
        String parameterName = parameter == null ? null : parameterDao.getKeyFromRelaxedName(parameter.getName());
        String unitName = unit == null ? null : unitDao.getKeyFromRelaxedName(unit.getName());

        // Must have a return type
        if (returnType == null) {
            return DependencyValidationResult.NO_RETURN_TYPE;
        }

        // For the return type evaluate the results of the releases and transfers cache
        Map<String, Map<String, Set<String>>> cacheReleasesAndTransfersCache = dao.getCache().get(returnTypeName);

        if (releasesAndTransfers == null) {
            // Ignoring releases and transfers
            // We require a inverse-wildcard in the map here
            if (cacheReleasesAndTransfersCache.containsKey("^*")) {
                return validate(cacheReleasesAndTransfersCache.get("^*"), parameterName, unitName);
            } else {
                return DependencyValidationResult.BAD_DATA;
            }
        } else {
            // The order is important - first look for specific inclusion
            // Then the exclusion, then the wildcard - otherwise it is
            if (cacheReleasesAndTransfersCache.containsKey(releasesAndTransfersName)) {
                // Found - Ok
                return validate(cacheReleasesAndTransfersCache.get(releasesAndTransfersName), parameterName, unitName);
            } if (cacheReleasesAndTransfersCache.containsKey("^" + releasesAndTransfersName)) {
                // Test for specific exclusion
                return DependencyValidationResult.BAD_RELEASE;
            } if (cacheReleasesAndTransfersCache.containsKey("*")) {
                // Wildcard allow any
                return validate(cacheReleasesAndTransfersCache.get("*"), parameterName, unitName);
            } else if (cacheReleasesAndTransfersCache.containsKey("^*")) {
                return DependencyValidationResult.RELEASE_NOT_APPLICABLE;
            } else {
                return DependencyValidationResult.BAD_DATA;
            }
        }
    }

    /*
     * Continue validation at the parameter level
     */
    private DependencyValidationResult validate(Map<String, Set<String>> parameterCache,
                                                String parameterName,
                                                String unitName) {

        // We have to have a parameter
        if (parameterName == null) {
            return DependencyValidationResult.NO_PARAMETER;
        } else {
            // Test the parameter is excluded OR is in the appropriate list there is a wildcard
            if (parameterCache.containsKey(parameterName)) {
                // Found - Ok
                return validate(parameterCache.get(parameterName), unitName);
            } else if (parameterCache.containsKey("^" + parameterName)) {
                // Specific exclusion
                return DependencyValidationResult.BAD_PARAMETER;
            } else if (parameterCache.containsKey("*")) {
                // Wildcard
                return validate(parameterCache.get("*"), unitName);
            } else {
                return DependencyValidationResult.BAD_DATA;
            }
        }
    }

    /*
     * Continue with the validation of the units
     */
    private DependencyValidationResult validate(Set<String> unitCache, String unitName) {
        if (unitName == null) {
            // Ok not to have a unit (Unless ^* is specified)
            return DependencyValidationResult.OK;
        } else {
            if (unitCache.contains(unitName)) {
                return DependencyValidationResult.OK;
            } else if (unitCache.contains("^" + unitName)) {
                // Units explicitly disallowed
                return DependencyValidationResult.BAD_UNITS;
            } else if (unitCache.contains("*")) {
                // Units on wildcard - all allowed
                return DependencyValidationResult.OK;
            } else if (unitCache.contains("^*")) {
                // Units on inverse wildcard - not allowed
                return DependencyValidationResult.UNIT_NOT_APPLICABLE;
            } else {
                return DependencyValidationResult.BAD_DATA;
            }
        }
    }

    /*
     * These overrides deal with varying input
     */
    public DependencyValidationResult validate(ReturnType returnType, Parameter parameter, Unit unit) {
        return validate(returnType, null, parameter, unit);
    }

    public DependencyValidationResult validate(ReturnType returnType, Parameter parameter) {
         return validate(returnType, null, parameter, null);
    }

}
