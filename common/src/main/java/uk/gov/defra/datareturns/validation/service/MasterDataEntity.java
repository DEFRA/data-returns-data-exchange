package uk.gov.defra.datareturns.validation.service;

import lombok.Getter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.UriTemplate;

@Getter
public enum MasterDataEntity {
    SITES("sites"),
    OPERATORS("operators"),
    UNIQUE_IDENTIFIERS("uniqueIdentifiers"),
    AREAS("areas"),
    REGIONS("regions"),

    NOSE_ACTIVITIES("noseActivities"),
    NOSE_ACTIVITY_CLASSES("noseActivityClasses"),


    NACE_SECTIONS("naceSections"),
    NACE_DIVISIONS("naceDivisions"),
    NACE_GROUPS("naceGroups"),
    NACE_CLASSES("naceClasses"),

    REGIMES("regimes"),
    REGIME_OBLIGATIONS("regimeObligations"),
    ROUTES("routes"),
    SUB_ROUTES("subroutes"),

    PARAMETERS("parameters"),
    PARAMETER_GROUPS("parameterGroups"),
    PARAMETER_TYPES("parameterTypes"),

    UNITS("units"),
    UNIT_TYPES("unitTypes"),


    THRESHOLDS("thresholds"),


    REFERENCE_PERIODS("referencePeriods"),
    METHODS_OR_STANDARDS("methodOrStandards"),
    QUALIFIERS("qualifiers"),
    RETURN_TYPES("returnTypes"),
    RETURN_PERIODS("returnPeriods"),
    TEXT_VALUES("textValues"),


    EPRTR_SECTORS("eprtrSectors"),
    EPRTR_ACTIVITIES("eprtrActivities"),

    WFD_DISPOSAL_CODES("disposalCodes"),
    WFD_RECOVERY_CODES("recoveryCodes"),

    EWC_CHAPTERS("ewcChapters"),
    EWC_SUBCHAPTERS("ewcSubchapters"),
    EWC_ACTIVITIES("ewcActivities");

    private final Link collectionLink;
    private final Link entityLink;

    MasterDataEntity(final String resourceName) {
        collectionLink = new Link(new UriTemplate(resourceName), resourceName);
        entityLink = new Link(new UriTemplate(resourceName + "/{id}"), resourceName);
    }
}
