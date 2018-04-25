package uk.gov.defra.datareturns.validation.service;

import lombok.Getter;
import org.atteo.evo.inflector.English;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.UriTemplate;
import uk.gov.defra.datareturns.validation.service.dto.MdBaseEntity;
import uk.gov.defra.datareturns.validation.service.dto.MdParameter;
import uk.gov.defra.datareturns.validation.service.dto.MdParameterGroup;
import uk.gov.defra.datareturns.validation.service.dto.MdReferencePeriod;
import uk.gov.defra.datareturns.validation.service.dto.MdRoute;
import uk.gov.defra.datareturns.validation.service.dto.MdSite;
import uk.gov.defra.datareturns.validation.service.dto.MdSubroute;
import uk.gov.defra.datareturns.validation.service.dto.MdTextValue;
import uk.gov.defra.datareturns.validation.service.dto.MdUniqueIdentifier;
import uk.gov.defra.datareturns.validation.service.dto.MdUnit;

@Getter
public enum MasterDataEntity {
    SITE("site", MdSite.class),
    OPERATOR("operator"),
    UNIQUE_IDENTIFIER("uniqueIdentifier", MdUniqueIdentifier.class),
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
    ROUTE("route", MdRoute.class),
    SUB_ROUTE("subroute", MdSubroute.class),

    PARAMETER("parameter", MdParameter.class),
    PARAMETER_GROUP("parameterGroup", MdParameterGroup.class),
    PARAMETER_TYPE("parameterType"),

    UNIT("unit", MdUnit.class),
    UNIT_TYPE("unitType"),

    THRESHOLD("threshold"),

    REFERENCE_PERIOD("referencePeriod", MdReferencePeriod.class),
    METHOD_OR_STANDARD("methodOrStandard"),
    QUALIFIER("qualifier"),
    RETURN_TYPE("returnType"),
    // TODO - Return period isn't really a controlled list! It should be removed from the API and the ECM frontend should describe its use
    // via static documentation rather than as a controlled list!
//    RETURN_PERIOD("returnPeriod"),
    TEXT_VALUE("textValue", MdTextValue.class),

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
    private final Class<? extends MdBaseEntity> defaultType;

    MasterDataEntity(final String resourceName) {
        this(resourceName, MdBaseEntity.class);
    }

    MasterDataEntity(final String resourceName, final Class<? extends MdBaseEntity> defaultType) {
        this.resourceName = resourceName;
        this.itemRel = resourceName;
        this.collectionRel = English.plural(resourceName);
        this.resourcePath = this.collectionRel;

        this.collectionLink = new Link(new UriTemplate(this.resourcePath), resourceName);
        this.entityLink = new Link(new UriTemplate(this.resourcePath + "/{id}"), resourceName);
        this.defaultType = defaultType;
    }
}
