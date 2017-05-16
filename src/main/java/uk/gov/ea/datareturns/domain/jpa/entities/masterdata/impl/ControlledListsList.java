package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import uk.gov.ea.datareturns.domain.dto.impl.DisplayHeaderDto;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.*;

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

    RETURN_TYPE("Return type", ReturnTypeDao.class, "Rtn_Type",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Rtn_Type"));
            }})),

    REFERENCE_PERIOD("Reference period", ReferencePeriodDao.class, "Ref_Period",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Ref_Period"));
                add(new DisplayHeaderDto("aliases", "Alternatives"));
                add(new DisplayHeaderDto("notes", "Notes"));
            }})),

    RETURN_PERIOD("Return period", ReturnPeriodDao.class, "Rtn_Period",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Rtn_Period"));
                add(new DisplayHeaderDto("definition", "Definition"));
                add(new DisplayHeaderDto("example", "Example"));
            }})),

    PARAMETER("Parameter (substance name)", ParameterDao.class, "Parameter",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Parameter"));
                add(new DisplayHeaderDto("aliases", "Alternatives"));
                add(new DisplayHeaderDto("cas", "CAS"));
            }})),

    UNIT("Unit or measure", UnitDao.class, "Unit",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Unit"));
                add(new DisplayHeaderDto("aliases", "Alternatives"));
                add(new DisplayHeaderDto("longName", "Long Name"));
                add(new DisplayHeaderDto("type", "BasicMeasurement Type"));
                add(new DisplayHeaderDto("unicode", "Unicode"));
                add(new DisplayHeaderDto("description", "Definition"));
            }})),

    QUALIFIER("Qualifier", QualifierDao.class, "Qualifier",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Qualifier"));
                add(new DisplayHeaderDto("notes", "Notes"));
                add(new DisplayHeaderDto("type", "Type"));
                add(new DisplayHeaderDto("singleOrMultiple", "Single or multiple"));
            }})),

    METHOD_OR_STANDARD("Monitoring standard or method", MethodOrStandardDao.class, "Meth_Stand",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Meth_Stand"));
                add(new DisplayHeaderDto("notes", "Notes"));
            }})),

    TEXT_VALUES("Text value", TextValueDao.class, "Txt_Value",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Txt_Value"));
                add(new DisplayHeaderDto("aliases", "Alternatives"));
            }})),

    RELEASES_AND_TRANSFER("Releases and transfers", ReleasesAndTransfersDao.class, "Rel_Trans",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                // TODO: Added for PI and subsequently disabled due to change of focus
                //                add(new DisplayHeaderDto("name", "Rel_Trans"));
            }}));

    private final String path;

    private final Class<? extends EntityDao> daoCls;
    private final String description;
    private static final Map<String, ControlledListsList> byPath = new HashMap<>();
    private final List<DisplayHeaderDto> displayHeaders; // Column name to column heading

    ControlledListsList(String description, Class<? extends EntityDao> daoCls,
            String path, List<DisplayHeaderDto> displayHeaders) {
        this.description = description;
        this.path = path;
        this.daoCls = daoCls;
        this.displayHeaders = displayHeaders;
    }

    static {
        for (ControlledListsList c : ControlledListsList.values()) {
            byPath.put(c.path, c);
        }
    }

    public static ControlledListsList getByPath(String path) {
        return byPath.get(path);
    }

    public Class<? extends EntityDao> getDaoCls() {
        return daoCls;
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
}
