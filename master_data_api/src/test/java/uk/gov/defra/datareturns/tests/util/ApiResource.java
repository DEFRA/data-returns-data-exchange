package uk.gov.defra.datareturns.tests.util;

public enum ApiResource {
    APPLICABILITIES("applicabilities"),
    UNIQUE_IDENTIFIER_GROUPS("uniqueIdentifierGroups"),
    UNIQUE_IDENTIFIERS("uniqueIdentifiers"),
    SITES("sites"),
    RETURN_TYPE_GROUPS("returnTypeGroups"),
    RETURN_TYPES("returnTypes"),
    PARAMETER_GROUPS("parameterGroups"),
    PARAMETERS("parameters"),
    UNIT_GROUPS("unitGroups"),
    UNITS("units"),
    UNIT_TYPES("unitsTypes");

    private final String resourceName;

    private ApiResource(String resourceName) {
        this.resourceName = resourceName;
    }

    public String url() {
        return '/' + this.resourceName;
    }


    public String resourceName() {
        return this.resourceName;
    }
}
