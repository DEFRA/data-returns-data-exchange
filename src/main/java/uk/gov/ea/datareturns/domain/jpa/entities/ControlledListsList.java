package uk.gov.ea.datareturns.domain.jpa.entities;

import uk.gov.ea.datareturns.domain.dto.DisplayHeaderDto;
import uk.gov.ea.datareturns.domain.jpa.dao.*;

import java.util.*;

/**
 * @Author Graham Willis
 * This is an enum which is used for generic functionality around the controlled lists
 * - those entities which are controlled lists in the DEP and have the associated
 * functionality in the front end. EA_ID and sites are also treated as controlled lists
 * but only to piggy back the cache search functionality. They are not listed here as they
 * do not form part of the DEP
 */
public enum ControlledListsList {

    RETURN_TYPE("Return type", ReturnTypeDao.class, "Rtn_Type", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Rtn_Type"));
    }}), "name"),

    REFERENCE_PERIOD("Reference period", ReferencePeriodDao.class, "Ref_Period",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Ref_Period"));
                add(new DisplayHeaderDto("aliases", "Alternatives"));
                add(new DisplayHeaderDto("notes", "Notes"));
            }}), "name"),

    RETURN_PERIOD("Return period", ReturnPeriodDao.class, "Rtn_Period", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Rtn_Period"));
        add(new DisplayHeaderDto("definition", "Definition"));
        add(new DisplayHeaderDto("example", "Example"));
    }}), "name"),

    PARAMETER("Parameter (substance name)", ParameterDao.class, "Parameter",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Parameter"));
                add(new DisplayHeaderDto("aliases", "Alternatives"));
                add(new DisplayHeaderDto("cas", "CAS"));
            }}), "name"),

    UNIT("Unit or measure", UnitDao.class, "Unit", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Unit"));
        add(new DisplayHeaderDto("aliases", "Alternatives"));
        add(new DisplayHeaderDto("longName", "Long Name"));
        add(new DisplayHeaderDto("type", "Measurement Type"));
        add(new DisplayHeaderDto("unicode", "Unicode"));
        add(new DisplayHeaderDto("description", "Definition"));
    }}), "name"),

    QUALIFIER("Qualifier", QualifierDao.class, "Qualifier", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Qualifier"));
        add(new DisplayHeaderDto("notes", "Notes"));
        add(new DisplayHeaderDto("type", "Type"));
        add(new DisplayHeaderDto("singleOrMultiple", "Single or multiple"));
    }}), "name"),

    METHOD_OR_STANDARD("Monitoring standard or method", MethodOrStandardDao.class, "Meth_Stand",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Meth_Stand"));
                add(new DisplayHeaderDto("notes", "Notes"));
            }}), "name"),

    TEXT_VALUES("Text value", TextValueDao.class, "Txt_Value", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Txt_Value"));
        add(new DisplayHeaderDto("aliases", "Alternatives"));
    }}), "name"),

    RELEASES_AND_TRANSFER("Releases and transfers", ReleasesAndTransfersDao.class, "Rel_Trans",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Rel_Trans"));
    }}), "name");

    private final String path;

    private final Class<? extends EntityDao> dao;
    private final String defaultSearch;
    private final String description;
    private static final Map<String, ControlledListsList> byPath = new HashMap<>();
    private final List<DisplayHeaderDto> displayHeaders; // Column name to column heading

    ControlledListsList(String description, Class<? extends EntityDao> dao,
                        String path, List<DisplayHeaderDto> displayHeaders, String defaultSearch) {
        this.description = description;
        this.path = path;
        this.dao = dao;
        this.displayHeaders = displayHeaders;
        this.defaultSearch = defaultSearch;
    }

    static {
        for (ControlledListsList c : ControlledListsList.values()) {
            byPath.put(c.path, c);
        }
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
