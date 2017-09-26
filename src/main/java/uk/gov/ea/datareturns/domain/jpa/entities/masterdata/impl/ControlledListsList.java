package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import com.querydsl.core.types.dsl.StringPath;
import uk.gov.ea.datareturns.domain.dto.impl.DisplayHeaderDto;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Graham Willis
 * This is an enum which is used for generic functionality around the controlled lists
 * - those entities which are controlled lists in the DEP and have the associated
 * functionality in the front end. EA_ID and sites are also treated as controlled lists
 * but only to piggy back the cache search functionality. They are not listed here as they
 * do not form part of the DEP
 */
public enum ControlledListsList {

    RETURN_TYPE("Return type", ReturnType.class, "Rtn_Type",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Rtn_Type"));
            }}),
            Collections.singletonList(QReturnType.returnType.name)
    ),

    REFERENCE_PERIOD("Reference period", ReferencePeriod.class, "Ref_Period",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Ref_Period"));
                add(new DisplayHeaderDto("aliases", "Alternatives"));
                add(new DisplayHeaderDto("notes", "Notes"));
            }}),
            Arrays.asList(
                    QReferencePeriod.referencePeriod.name,
                    QReferencePeriod.referencePeriod.aliases.any().name
            )
    ),

    RETURN_PERIOD("Return period", ReturnPeriod.class, "Rtn_Period",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Rtn_Period"));
                add(new DisplayHeaderDto("definition", "Definition"));
                add(new DisplayHeaderDto("example", "Example"));
            }}),
            Collections.singletonList(QReturnPeriod.returnPeriod.name)
    ),

    PARAMETER("Parameter (substance name)", Parameter.class, "Parameter",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Parameter"));
                add(new DisplayHeaderDto("aliases", "Alternatives"));
                add(new DisplayHeaderDto("cas", "CAS"));
            }}),
            Arrays.asList(
                    QParameter.parameter.name,
                    QParameter.parameter.aliases.any().name,
                    QParameter.parameter.cas
            )
    ),

    UNIT("Unit or measure", Unit.class, "Unit",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Unit"));
                add(new DisplayHeaderDto("aliases", "Alternatives"));
                add(new DisplayHeaderDto("longName", "Long Name"));
                add(new DisplayHeaderDto("type", "AlternativePayload Type"));
                add(new DisplayHeaderDto("unicode", "Unicode"));
                add(new DisplayHeaderDto("description", "Definition"));
            }}),
            Arrays.asList(
                    QUnit.unit.name,
                    QUnit.unit.aliases.any().name,
                    QUnit.unit.longName,
                    QUnit.unit.type
            )
    ),

    QUALIFIER("Qualifier", Qualifier.class, "Qualifier",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Qualifier"));
                add(new DisplayHeaderDto("notes", "Notes"));
                add(new DisplayHeaderDto("type", "Type"));
                add(new DisplayHeaderDto("singleOrMultiple", "Single or multiple"));
            }}),
            Collections.singletonList(QQualifier.qualifier.name)
    ),

    METHOD_OR_STANDARD("Monitoring standard or method", MethodOrStandard.class, "Meth_Stand",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Meth_Stand"));
                add(new DisplayHeaderDto("notes", "Notes"));
            }}),
            Collections.singletonList(QMethodOrStandard.methodOrStandard.name)
    ),

    TEXT_VALUES("Text value", TextValue.class, "Txt_Value",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                add(new DisplayHeaderDto("name", "Txt_Value"));
                add(new DisplayHeaderDto("aliases", "Alternatives"));
            }}),
            Arrays.asList(
                    QTextValue.textValue.name,
                    QTextValue.textValue.aliases.any().name
            )
    ),
    RELEASES_AND_TRANSFER("Releases and transfers", ReleasesAndTransfers.class, "Rel_Trans",
            Collections.unmodifiableList(new ArrayList<DisplayHeaderDto>() {{
                // TODO: Added for PI and subsequently disabled due to change of focus
                //                add(new DisplayHeaderDto("name", "Rel_Trans"));
            }}),
            Collections.singletonList(QReleasesAndTransfers.releasesAndTransfers.name)
    );

    private final String path;

    private final Class<? extends MasterDataEntity> entityClass;
    private final String description;
    private static final Map<String, ControlledListsList> byPath = new HashMap<>();
    private final List<DisplayHeaderDto> displayHeaders; // Column name to column heading
    private final List<StringPath> searchablePaths;
    private final Set<String> searchFields;

    ControlledListsList(String description, Class<? extends MasterDataEntity> entityClass,
            String path, List<DisplayHeaderDto> displayHeaders, List<StringPath> searchablePaths) {
        this.description = description;
        this.path = path;
        this.entityClass = entityClass;
        this.displayHeaders = displayHeaders;
        this.searchablePaths = searchablePaths;

        // Pattern to match the case where we are searching for a property that is nested within a collection on the top level entity
        Pattern pathMatcher = Pattern.compile("any\\([^.]+.([^)]+)\\).*");
        this.searchFields = new HashSet<>();
        for (StringPath stringPath : searchablePaths) {
            String pathString = stringPath.toString();
            Matcher m = pathMatcher.matcher(pathString);
            if (m.matches()) {
                this.searchFields.add(m.group(1));
            } else {
                this.searchFields.add(stringPath.getMetadata().getName());
            }
        }

    }

    static {
        for (ControlledListsList c : ControlledListsList.values()) {
            byPath.put(c.path, c);
        }
    }

    public static ControlledListsList getByPath(String path) {
        return byPath.get(path);
    }

    public Class<? extends MasterDataEntity> getEntityClass() {
        return entityClass;
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

    public List<StringPath> getSearchablePaths() {
        return searchablePaths;
    }

    public Set<String> getSearchableFields() {
        return searchFields;
    }
}
