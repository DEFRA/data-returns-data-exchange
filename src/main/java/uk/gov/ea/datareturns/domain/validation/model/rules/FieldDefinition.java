package uk.gov.ea.datareturns.domain.validation.model.rules;

import uk.gov.ea.datareturns.domain.validation.model.DataSample;
import uk.gov.ea.datareturns.domain.validation.model.fields.FieldValue;
import uk.gov.ea.datareturns.domain.validation.model.fields.impl.ds.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Provides definitions for all entityfields defined in the DEP
 *
 * @author Sam Gardner-Dell
 */
// TODO - GMW remove - this is redundant in the new API
public enum FieldDefinition {
    /** The EA Unique Identifier (EA_ID) */
    EA_ID("EA_ID", EaId.class),

    /** The site name (Site_Name) */
    Site_Name("Site_Name", SiteName.class),

    /** The return type (Rtn_Type) */
    Rtn_Type("Rtn_Type", ReturnType.class),

    /** The monitoring date (Mon_Date) */
    Mon_Date("Mon_Date", MonitoringDate.class),

    /** The return period (Rtn_Period) */
    Rtn_Period("Rtn_Period", ReturnPeriod.class),

    /** The monitoring point (Mon_Point) */
    Mon_Point("Mon_Point", MonitoringPoint.class),
    //
    //    /** Sample reference (Smpl_Ref) */
    //    Smpl_Ref("Smpl_Ref", null),
    //
    //    /** Sampled by (Smpl_By) */
    //    Smpl_By("Smpl_By", null),

    /** Parameter value (Parameter) */
    Parameter("Parameter", Parameter.class),

    /** Qualifier (Qualifier) */
    Qualifier("Qualifier", uk.gov.ea.datareturns.domain.validation.model.fields.impl.ds.Qualifier.class),

    /** Value (Value) */
    Value("Value", uk.gov.ea.datareturns.domain.validation.model.fields.impl.ds.Value.class),

    /** Textual value (Txt_Value) */
    Txt_Value("Txt_Value", TxtValue.class),

    /** Unit of measurement (Unit) */
    Unit("Unit", uk.gov.ea.datareturns.domain.validation.model.fields.impl.ds.Unit.class),

    /** Reference period */
    Ref_Period("Ref_Period", ReferencePeriod.class),

    /** Method or standard used (Meth_Stand) */
    Meth_Stand("Meth_Stand", MethodOrStandard.class),

    /** RecordEntity comments (Comments) */
    Comments("Comments", uk.gov.ea.datareturns.domain.validation.model.fields.impl.ds.Comments.class),

    /** Commercial in confidence data (CiC) */
    CiC("CiC", Cic.class),

    /** Releases and transfers */
    Rel_Trans("Rel_Trans", ReleasesAndTransfers.class);

    //    /** Chemical Abstracts Service value (CAS) */
    //    CAS("CAS", null),
    //
    //    /** Recovery and disposal code (RD_Code) */
    //    RD_Code("RD_Code", null);

    /** An array of all field names */
    public static final String[] ALL_FIELD_NAMES_ARR = Arrays.stream(values()).map(FieldDefinition::getName)
            .toArray(len -> new String[len]);

    /** A set of all field names */
    public static final Set<String> ALL_FIELD_NAMES = Collections.unmodifiableSet(
            new LinkedHashSet<>(Arrays.asList(ALL_FIELD_NAMES_ARR)));

    /** The set of entityfields which must always be present in the source data */
    public final static Set<FieldDefinition> MANDATORY_FIELDS = Collections.unmodifiableSet(new LinkedHashSet<>(
            Arrays.asList(EA_ID, Site_Name, Rtn_Type, Mon_Date, Mon_Point, Parameter)));

    /** An array of mandatory field names */
    public static final String[] MANDATORY_FIELD_NAMES_ARR = MANDATORY_FIELDS.stream().map(FieldDefinition::getName)
            .toArray(len -> new String[len]);

    /** A set of all mandatory field names */
    public static final Set<String> MANDATORY_FIELD_NAMES = Collections.unmodifiableSet(
            new LinkedHashSet<>(Arrays.asList(MANDATORY_FIELD_NAMES_ARR)));

    private final String name;
    private final Class<? extends FieldValue<DataSample, ?>> type;

    /**
     * Create a new FieldDefinition
     *
     * @param name the field name (heading)
     * @param type the data type of the field
     */
    FieldDefinition(final String name, final Class<? extends FieldValue<DataSample, ?>> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<? extends FieldValue> getType() {
        return type;
    }

    public static FieldDefinition forType(Class<?> type) {
        return Arrays.stream(values()).filter(f -> f.getType().equals(type)).findFirst().orElse(null);
    }

    public static FieldDefinition forFieldName(String fieldName) {
        return Arrays.stream(values()).filter(f -> f.getName().equals(fieldName)).findFirst().orElse(null);
    }
}
