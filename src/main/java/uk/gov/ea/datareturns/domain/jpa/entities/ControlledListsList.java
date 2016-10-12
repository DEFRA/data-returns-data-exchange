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
    }}), "name", 0),

    REFERENCE_PERIOD("Reference period", ReferencePeriodDao.class, "ref_period",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Ref_Period"));
                add(new DisplayHeaderDto("aliases", "Alternatives"));
                add(new DisplayHeaderDto("notes", "Notes"));
            }}), "name"),

    RETURN_PERIOD("Return period", ReturnPeriodDao.class, "rtn_period", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Rtn_Period"));
        add(new DisplayHeaderDto("definition", "Definition"));
        add(new DisplayHeaderDto("example", "Example"));
    }}), "name"),

    PARAMETERS("Parameter (substance name)", ParameterDao.class, "parameters",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Parameter"));
                add(new DisplayHeaderDto("aliases", "Alternatives"));
                add(new DisplayHeaderDto("cas", "CAS"));
            }}), "name", 2),

    UNITS("Unit or measure", UnitDao.class, "units", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Unit"));
        add(new DisplayHeaderDto("aliases", "Alternatives"));
        add(new DisplayHeaderDto("longName", "Long Name"));
        add(new DisplayHeaderDto("type", "Measurement Type"));
        add(new DisplayHeaderDto("unicode", "Unicode"));
        add(new DisplayHeaderDto("description", "Definition"));
    }}), "name", 3),

    QUALIFIERS("Qualifier", QualifierDao.class, "qualifier", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Qualifier"));
        add(new DisplayHeaderDto("notes", "Notes"));
        add(new DisplayHeaderDto("type", "Type"));
        add(new DisplayHeaderDto("singleOrMultiple", "Single or multiple"));
    }}), "name"),

    METHOD_OR_STANDARD("Monitoring standard or method", MethodOrStandardDao.class, "method",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Meth_Stand"));
                add(new DisplayHeaderDto("notes", "Notes"));
            }}), "name"),

    TEXT_VALUES("Text value", TextValueDao.class, "txt_value", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Txt_Value"));
        add(new DisplayHeaderDto("aliases", "Alternatives"));
    }}), "name"),

    RELEASES_AND_TRANSFERS("Releases and transfers", ReleasesAndTransfersDao.class, "mon_point", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "Mon_point"));
    }}), "name", 1),

    UNIQUE_IDENTIFIER("Releases and transfers", UniqueIdentifierDao.class, "ea_id", Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
        add(new DisplayHeaderDto("name", "EA_ID"));
    }}), "name");

    private final String path;

    private static final Logger LOGGER = LoggerFactory.getLogger(ControlledListsList.class);
    private final Class<? extends EntityDao> dao;
    private final String defaultSearch;
    private final String description;
    private static final Map<String, ControlledListsList> byPath = new HashMap<>();
    private final List<DisplayHeaderDto> displayHeaders; // Column name to column heading
    private Integer hierarchyLevel = null;

    public static Comparator<ControlledListsList> hierarchyOrder = Comparator
            .comparing(ControlledListsList::getHierarchyLevel);

    static {
        for (ControlledListsList c : ControlledListsList.values()) {
            byPath.put(c.path, c);
        }
    }

    public ControlledListsList next() {
        if (this.hierarchyLevel == null) {
            // Not on the hierarchy
            return null;
        } else {
            ControlledListsList[] arr = values();
            for (ControlledListsList cl : arr) {
                if (cl.hierarchyLevel != null && cl.hierarchyLevel == this.hierarchyLevel + 1) {
                    return cl;
                }
            }
            // Probably iterated off the end of the array.
            return null;
        }
    }

    ControlledListsList(String description, Class<? extends EntityDao> dao,
            String path, List<DisplayHeaderDto> displayHeaders, String defaultSearch) {
        this.description = description;
        this.path = path;
        this.dao = dao;
        this.displayHeaders = displayHeaders;
        this.defaultSearch = defaultSearch;
        this.hierarchyLevel = null;
    }

    ControlledListsList(String description, Class<? extends EntityDao> dao,
                        String path, List<DisplayHeaderDto> displayHeaders, String defaultSearch, Integer hierarchyLevel) {
        this.description = description;
        this.path = path;
        this.dao = dao;
        this.displayHeaders = displayHeaders;
        this.defaultSearch = defaultSearch;
        this.hierarchyLevel = hierarchyLevel;
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

    public Integer getHierarchyLevel() {
        return this.hierarchyLevel;
    }
}
