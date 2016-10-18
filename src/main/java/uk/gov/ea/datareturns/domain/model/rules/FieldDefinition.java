package uk.gov.ea.datareturns.domain.model.rules;

import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.fields.FieldValue;
import uk.gov.ea.datareturns.domain.model.fields.impl.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Provides definitions for all fields defined in the DEP
 *
 * @author Sam Gardner-Dell
 */
public enum FieldDefinition {
    /** The EA Unique Identifier (EA_ID) */
    EA_ID("EA_ID", EaId.class,
            "Your EA unique identifier: For permits - 2 capital letters, 4 numbers and 2 capital letters (eg, AB1234ZZ) or for Waste Management Licences - a 5 or 6 digit number (eg, 654321)"),

    /** The site name (Site_Name) */
    Site_Name("Site_Name", SiteName.class,
            "You must give your reference for the site as this field allows EA to cross check your site name against EA_ID"),

    /** The return type (Rtn_Type) */
    Rtn_Type("Rtn_Type", ReturnType.class, "The type of data being returned"),

    /** The monitoring date (Mon_Date) */
    Mon_Date("Mon_Date", MonitoringDate.class,
            "Monitoring date/time. This is the date and (optionally) time (eg, for a spot sample). If you're monitoring for a period of time this is the date/time at the end of the monitoring period"),

    /** The return period (Rtn_Period) */
    Rtn_Period("Rtn_Period", ReturnPeriod.class, "Name of date range covering the entire return"),

    /** The monitoring point (Mon_Point) */
    Mon_Point("Mon_Point", MonitoringPoint.class,
            "The monitoring point reference for where the sample was taken. Refer to the sampling or emission point described in your permit or licence"),
    //
    //    /** Sample reference (Smpl_Ref) */
    //    Smpl_Ref("Smpl_Ref", null, "Sample reference"),
    //
    //    /** Sampled by (Smpl_By) */
    //    Smpl_By("Smpl_By", null, "Sampled by"),

    /** Parameter value (Parameter) */
    Parameter("Parameter", Parameter.class,
            "The chemical substance or parameter you're monitoring. Select an entry from the parameter controlled list"),

    /** Qualifier (Qualifier) */
    Qualifier("Qualifier", uk.gov.ea.datareturns.domain.model.fields.impl.Qualifier.class,
            "The qualifier gives further definition to a chosen parameter to make it more specific, for example 'Dry weight' or 'Wet weight'"),

    /** Value (Value) */
    Value("Value", uk.gov.ea.datareturns.domain.model.fields.impl.Value.class,
            "The numerical value of a measurement. As well as numbers, can include the symbols < or >"),

    /** Textual value (Txt_Value) */
    Txt_Value("Txt_Value", TxtValue.class,
            "A field for a text based response such as true, false, yes and no.  Select an entry from the controlled list."),

    /** Unit of measurement (Unit) */
    Unit("Unit", uk.gov.ea.datareturns.domain.model.fields.impl.Unit.class, "The unit or measure used for the given data return"),

    /** Reference period */
    Ref_Period("Ref_Period", ReferencePeriod.class,
            "The reference period for the sample describes how the sample was taken - eg, '24 hour total', 'Half hour average'"),

    /** Method or standard used (Meth_Stand) */
    Meth_Stand("Meth_Stand", MethodOrStandard.class, "Method or standard used for monitoring"),

    /** Record comments (Comments) */
    Comments("Comments", uk.gov.ea.datareturns.domain.model.fields.impl.Comments.class, "Free-text comments about the row of data"),

    /** Commercial in confidence data (CiC) */
    CiC("CiC", Cic.class, "Commercial in confidence");

    //    /** Chemical Abstracts Service value (CAS) */
    //    CAS("CAS", null, "Chemical Abstracts Service number"),
    //
    //    /** Recovery and disposal code (RD_Code) */
    //    RD_Code("RD_Code", null, "Recovery or disposal code");

    /** An array of all field names */
    public static final String[] ALL_FIELD_NAMES_ARR = Arrays.stream(values()).map(FieldDefinition::getName)
            .toArray(len -> new String[len]);

    /** A set of all field names */
    public static final Set<String> ALL_FIELD_NAMES = Collections.unmodifiableSet(
            new LinkedHashSet<>(Arrays.asList(ALL_FIELD_NAMES_ARR)));

    /** The set of fields which must always be present in the source data */
    public final static Set<FieldDefinition> MANDATORY_FIELDS = Collections.unmodifiableSet(new LinkedHashSet<>(
            Arrays.asList(EA_ID, Site_Name, Rtn_Type, Mon_Date, Mon_Point, Parameter)));

    /** An array of mandatory field names */
    public static final String[] MANDATORY_FIELD_NAMES_ARR = MANDATORY_FIELDS.stream().map(FieldDefinition::getName)
            .toArray(len -> new String[len]);

    /** A set of all mandatory field names */
    public static final Set<String> MANDATORY_FIELD_NAMES = Collections.unmodifiableSet(
            new LinkedHashSet<>(Arrays.asList(MANDATORY_FIELD_NAMES_ARR)));

    private final String name;
    private final String description;
    private final Class<? extends FieldValue<DataSample, ?>> type;

    /**
     * Create a new FieldDefinition
     *
     * @param name the field name (heading)
     * @param type the data type of the field
     * @param description the long description for the field (if required)
     */
    FieldDefinition(final String name, final Class<? extends FieldValue<DataSample, ?>> type, final String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Class<? extends FieldValue> getType() {
        return type;
    }

    public String getDescription() {
        return this.description;
    }

    public static FieldDefinition forType(Class<?> type) {
        return Arrays.stream(values()).filter(f -> f.getType().equals(type)).findFirst().orElse(null);
    }

    public static FieldDefinition forFieldName(String fieldName) {
        return Arrays.stream(values()).filter(f -> f.getName().equals(fieldName)).findFirst().orElse(null);
    }
}
