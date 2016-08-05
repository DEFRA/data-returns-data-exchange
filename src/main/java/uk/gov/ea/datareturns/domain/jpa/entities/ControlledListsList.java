package uk.gov.ea.datareturns.domain.jpa.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by graham on 26/07/16.
 */
public enum ControlledListsList {

    UNITS_AND_MEASURES("Units and measures", UnitDao.class, "units", Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("description", "Description");
        put("measureType", "Measurement Type");
        put("name", "Units");
    }}), "description"),

    PARAMETERS("Parameters - substance names - and CAS", ParameterDao.class, "parameters", Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("name", "Name");
    }}), "name"),

    REFERENCE_PERIOD("Reference period", ReferencePeriodDao.class, "ref_period", Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("name", "Name");
    }}), "name"),

    MONITORING_PERIOD("Monitoring period", MonitoringPeriodDao.class, "mon_period", Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("name", "Name");
    }}), "name"),

    METHOD_OR_STANDARD("Monitoring standard or method", MethodOrStandardDao.class, "method", Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("name", "Name");
    }}), "name"),

    RETURN_TYPE("Return type", ReturnTypeDao.class, "rtn_type", Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("name", "Name");
    }}), "name");

    private final String path;

    private static final Logger LOGGER = LoggerFactory.getLogger(ControlledListsList.class);
    private final Class<? extends AbstractJpaDao> dao;
    private final String defaultSearch;
    private String description;
    private static Map<String, ControlledListsList> byPath = new HashMap<>();
    private Map<String, String> displayHeaders; // Column name to column heading

    static {
        for (ControlledListsList c : ControlledListsList.values()) {
            byPath.put(c.path, c);
        }
    }

    ControlledListsList(String description, Class<? extends AbstractJpaDao> dao,
                        String path, Map<String, String> displayHeaders, String defaultSearch) {
        this.description = description;
        this.path = path;
        this.dao = dao;
        this.displayHeaders = displayHeaders;
        this.defaultSearch = defaultSearch;
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

    public String getPath() {
        return path;
    }

    public Map<String, String> getDisplayHeaders() {
        return displayHeaders;
    }

    public String getDefaultSearch() {
        return defaultSearch;
    }
}
