package uk.gov.ea.datareturns.domain.jpa.entities;

import uk.gov.ea.datareturns.domain.jpa.dao.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by graham on 26/07/16.
 */
public enum ControlledListsList {
    UNITS_AND_MEASURES("Units and measures", Unit.class, UnitDao.class, "units"),
    PARAMETERS("Parameters - substance names - and CAS", Parameter.class, ParameterDao.class, "parameters"),
    REFERENCE_PERIOD("Reference period", ReferencePeriod.class, ReferencePeriodDao.class, "ref_period"),
    MONITORING_PERIOD("Monitoring period", MonitoringPeriod.class, MonitoringPeriodDao.class, "mon_period"),
    METHOD_OR_STANDARD("Monitoring standard or method", MethodOrStandard.class, MethodOrStandardDao.class, "method"),
    RETURN_TYPE("Return type", ReturnType.class, ReturnTypeDao.class, "rtn_type");

    private final String path;
    private final Class<? extends AbstractJpaDao> dao;
    private String description;
    private Class<? extends ControlledList> entityClass;
    private static Map<String, ControlledListsList> byPath = new HashMap<>();

    static {
        for (ControlledListsList c : ControlledListsList.values()) {
            byPath.put(c.path, c);
        }
    }

    ControlledListsList(String description, Class<? extends ControlledList> entityClass, Class<? extends AbstractJpaDao> dao, String path) {
        this.description = description;
        this.entityClass = entityClass;
        this.path = path;
        this.dao = dao;
    }

    public static ControlledListsList getByPath(String path) {
        return byPath.get(path);
    }

}
