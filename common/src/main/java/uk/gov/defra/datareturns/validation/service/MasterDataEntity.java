package uk.gov.defra.datareturns.validation.service;

import lombok.Getter;
import org.atteo.evo.inflector.English;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.UriTemplate;

@Getter
public enum MasterDataEntity {
    SITE("site"),
    OPERATOR("operator"),
    UNIQUE_IDENTIFIER("uniqueIdentifier"),
    AREA("area"),
    REGION("region"),

    NOSE_ACTIVITY("noseActivity"),
    NOSE_ACTIVITY_CLASS("noseActivityClass"),

    NACE_SECTION("naceSection"),
    NACE_DIVISION("naceDivision"),
    NACE_GROUP("naceGroup"),
    NACE_CLASS("naceClasse"),

    REGIME("regime"),
    REGIME_OBLIGATION("regimeObligation"),
    ROUTE("route"),
    SUB_ROUTE("subroute"),

    PARAMETER("parameter"),
    PARAMETER_GROUP("parameterGroup"),
    PARAMETER_TYPE("parameterType"),

    UNIT("unit"),
    UNIT_TYPE("unitType"),

    THRESHOLD("threshold"),

    REFERENCE_PERIOD("referencePeriod"),
    METHOD_OR_STANDARD("methodOrStandard"),
    QUALIFIER("qualifier"),
    RETURN_TYPE("returnType"),
    RETURN_PERIOD("returnPeriod"),
    TEXT_VALUE("textValue"),

    EPRTR_SECTOR("eprtrSector"),
    EPRTR_ACTIVITY("eprtrActivity"),

    WFD_DISPOSAL_CODE("disposalCode"),
    WFD_RECOVERY_CODE("recoveryCode"),

    EWC_CHAPTER("ewcChapter"),
    EWC_SUBCHAPTER("ewcSubchapter"),
    EWC_ACTIVITY("ewcActivity");

    private final String resourceName;
    private final String resourcePath;
    private final String itemRel;
    private final String collectionRel;
    private final Link collectionLink;
    private final Link entityLink;

    MasterDataEntity(final String resourceName) {
        this.resourceName = resourceName;
        this.itemRel = resourceName;
        this.collectionRel = English.plural(resourceName);
        this.resourcePath = this.collectionRel;

        this.collectionLink = new Link(new UriTemplate(this.resourcePath), resourceName);
        this.entityLink = new Link(new UriTemplate(this.resourcePath + "/{id}"), resourceName);
    }
}
