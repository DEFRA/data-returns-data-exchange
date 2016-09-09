package uk.gov.ea.datareturns.domain.model.rules;

/**
 * TODO: Move this stuff back into the frontend where it belongs!
 *
 * @author Sam Gardner-Dell
 */
public enum FieldDefinition {
    /** The EA Unique Identifier (EA_ID) */
    EA_ID("Environment Agency unique identifier"),

    /** The site name (Site_Name) */
    Site_Name("Site name"),

    /** The return type (Rtn_Type) */
    Rtn_Type("Return type"),

    /** The monitoring date (Mon_Date) */
    Mon_Date("Monitoring date"),

    /** The return period (Rtn_Period) */
    Rtn_Period("Return period"),

    /** The monitoring point (Mon_Point) */
    Mon_Point("Monitor point reference"),

    /** Sample reference (Smpl_Ref) */
    Smpl_Ref("Sample reference"),

    /** Sampled by (Smpl_By) */
    Smpl_By("Sampled by"),

    /** Parameter value (Parameter) */
    Parameter("Parameter"),

    /** Value (Value) */
    Value("Value"),

    /** Textual value (Txt_Value) */
    Txt_Value("Text value"),

    /** Unit of measurement (Unit) */
    Unit("Unit"),

    /** Reference period */
    Ref_Period("Reference period"),

    /** Method or standard used (Meth_Stand) */
    Meth_Stand("Monitoring method or standard"),

    /** Record comments (Comments) */
    Comments("Comments"),

    /** Commercial in confidence data (CiC) */
    CiC("Commercial in confidence"),

    /** Chemical Abstracts Service value (CAS) */
    CAS("Chemical Abstracts Service number"),

    /** Recovery and disposal code (RD_Code) */
    RD_Code("Recovery or disposal code"),

    /** Qualifier (Qualifier) */
    Qualifier("Qualifier");

    private final String description;

    /**
     * Create a new FieldDefinition
     *
     * @param description the long description for the field (if required)
     * @param helpReference the reference to the help page for the field definition
     */
    FieldDefinition(final String description) {
        this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

}
