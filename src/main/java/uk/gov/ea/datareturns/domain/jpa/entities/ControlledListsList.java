package uk.gov.ea.datareturns.domain.jpa.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.dto.DisplayHeaderDto;
import uk.gov.ea.datareturns.domain.jpa.dao.*;

import java.util.*;

/**
 * Created by graham on 26/07/16.
 */
public enum ControlledListsList {

    RETURN_TYPE("Return type", ReturnTypeDao.class, "rtn_type", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Rtn_Type"));
    }}), "name"),

    REFERENCE_PERIOD("Reference period", ReferencePeriodDao.class, "ref_period", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Ref_Period"));
        add(new DisplayHeaderDto("aliases", "Alternatives"));
        add(new DisplayHeaderDto("notes", "Notes"));
    }}), "name"),

    RETURN_PERIOD("Return period", ReturnPeriodDao.class, "rtn_period", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Rtn_Period"));
        add(new DisplayHeaderDto("definition", "Definition"));
        add(new DisplayHeaderDto("example", "Example"));
    }}), "name"),

    PARAMETERS("Parameters - substance names - and CAS", ParameterDao.class, "parameters", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Parameter"));
        add(new DisplayHeaderDto("aliases", "Alternatives"));
        add(new DisplayHeaderDto("cas", "CAS"));
    }}), "name"),

    UNITS_AND_MEASURES("Unit or measure", UnitDao.class, "units", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Unit"));
        add(new DisplayHeaderDto("aliases", "Alternatives"));
        add(new DisplayHeaderDto("longName", "Long Name"));
        add(new DisplayHeaderDto("type", "Measurement Type"));
        add(new DisplayHeaderDto("unicode", "Unicode"));
        add(new DisplayHeaderDto("description", "Definition"));
    }}), "name"),

    QUALIFIERS("Qualifier", QualifierDao.class, "qualifier", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Qualifier"));
        add(new DisplayHeaderDto("notes", "Notes"));
        add(new DisplayHeaderDto("type", "Type"));
        add(new DisplayHeaderDto("singleOrMultiple", "Single or multiple"));
    }}), "name"),

    METHOD_OR_STANDARD("Monitoring standard or method", MethodOrStandardDao.class, "method", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Meth_Stand"));
        add(new DisplayHeaderDto("notes", "Notes"));
    }}), "name"),

    TEXT_VALUES("Text Values", TextValueDao.class, "txt_value", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Txt_Value"));
        add(new DisplayHeaderDto("aliases", "Alternatives"));
    }}), "name");

    private final String path;

    private static final Logger LOGGER = LoggerFactory.getLogger(ControlledListsList.class);
    private final Class<? extends EntityDao> dao;
    private final String defaultSearch;
    private final String description;
    private static final Map<String, ControlledListsList> byPath = new HashMap<>();
    private final List<DisplayHeaderDto> displayHeaders; // Column name to column heading

    static {
        for (ControlledListsList c : ControlledListsList.values()) {
            byPath.put(c.path, c);
        }
    }

    ControlledListsList(String description, Class<? extends EntityDao> dao,
                        String path, List<DisplayHeaderDto> displayHeaders, String defaultSearch) {
        this.description = description;
        this.path = path;
        this.dao = dao;
        this.displayHeaders = displayHeaders;
        this.defaultSearch = defaultSearch;
    }

    public static ControlledListsList getByPath(String path) {
        return byPath.get(path);
    }

    public Class<? extends EntityDao> getDao() {
        return dao;
    }

    public String getDescription() {
        return description;
    }

    public String getPath() {
        return path;
    }

    public List<DisplayHeaderDto> getDisplayHeaders() {
        return displayHeaders;
    }

    public String getDefaultSearch() {
        return defaultSearch;
    }
}
