package uk.gov.ea.datareturns.domain.jpa.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by graham on 26/07/16.
 */
public enum ControlledListsList {
    UNITS_AND_MEASURES("Units and measures", UnitDao.class, "units"),
    PARAMETERS("Parameters - substance names - and CAS", ParameterDao.class, "parameters"),
    REFERENCE_PERIOD("Reference period", ReferencePeriodDao.class, "ref_period"),
    MONITORING_PERIOD("Monitoring period", MonitoringPeriodDao.class, "mon_period"),
    METHOD_OR_STANDARD("Monitoring standard or method", MethodOrStandardDao.class, "method"),
    RETURN_TYPE("Return type", ReturnTypeDao.class, "rtn_type");
    private final String path;

    private static final Logger LOGGER = LoggerFactory.getLogger(ControlledListsList.class);
    private final Class<? extends AbstractJpaDao> dao;
    private String description;
    private static Map<String, ControlledListsList> byPath = new HashMap<>();

    static {
        for (ControlledListsList c : ControlledListsList.values()) {
            byPath.put(c.path, c);
        }
    }

    ControlledListsList(String description, Class<? extends AbstractJpaDao> dao, String path) {
        this.description = description;
        this.path = path;
        this.dao = dao;
    }

    public static ControlledListsList getByPath(String path) {
        return byPath.get(path);
    }

    public Class<? extends AbstractJpaDao> getDao() {
        return dao;
    }

    public String getDescription() {
        return description;
    }
}
