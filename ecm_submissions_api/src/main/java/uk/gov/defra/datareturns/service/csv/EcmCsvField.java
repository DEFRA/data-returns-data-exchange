package uk.gov.defra.datareturns.service.csv;

import lombok.Getter;
import uk.gov.defra.datareturns.validation.service.MasterDataEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Provides definitions for all fields defined in the DEP
 *
 * @author Sam Gardner-Dell
 */
@Getter
public enum EcmCsvField {
    /**
     * The EA Unique Identifier (EA_ID)
     */
    EA_ID("EA_ID", MasterDataEntity.UNIQUE_IDENTIFIER),
    /**
     * The site name (Site_Name)
     */
    Site_Name("Site_Name", MasterDataEntity.SITE),

    /**
     * The return type (Rtn_Type)
     */
    Rtn_Type("Rtn_Type", MasterDataEntity.RETURN_TYPE),

    /**
     * The monitoring date (Mon_Date)
     */
    Mon_Date("Mon_Date"),

    /**
     * The return period (Rtn_Period)
     */
    Rtn_Period("Rtn_Period"),

    /**
     * The monitoring point (Mon_Point)
     */
    Mon_Point("Mon_Point"),

    /**
     * Parameter value (Parameter)
     */
    Parameter("Parameter", MasterDataEntity.PARAMETER),

    /**
     * Qualifier (Qualifier)
     */
    Qualifier("Qualifier", MasterDataEntity.QUALIFIER),

    /**
     * Value (Value)
     */
    Value("Value"),

    /**
     * Textual value (Txt_Value)
     */
    Txt_Value("Txt_Value", MasterDataEntity.TEXT_VALUE),

    /**
     * Unit of measurement (Unit)
     */
    Unit("Unit", MasterDataEntity.UNIT),

    /**
     * Reference period
     */
    Ref_Period("Ref_Period", MasterDataEntity.REFERENCE_PERIOD),

    /**
     * Method or standard used (Meth_Stand)
     */
    Meth_Stand("Meth_Stand", MasterDataEntity.METHOD_OR_STANDARD),

    /**
     * Record comments (Comments)
     */
    Comments("Comments"),

    /**
     * Commercial in confidence data (CiC)
     */
    CiC("CiC");

    /**
     * The set of fields which must always be present in the source data
     */
    public static final Set<EcmCsvField> MANDATORY_FIELDS = Collections.unmodifiableSet(new LinkedHashSet<>(
            Arrays.asList(EA_ID, Site_Name, Rtn_Type, Mon_Date, Mon_Point, Parameter)));
    /**
     * An array of all field names
     */
    private static final String[] ALL_FIELD_NAMES_ARR = Arrays.stream(values()).map(EcmCsvField::getFieldName).toArray(String[]::new);
    /**
     * A set of all field names
     */
    public static final Set<String> ALL_FIELD_NAMES = Collections.unmodifiableSet(
            new LinkedHashSet<>(Arrays.asList(ALL_FIELD_NAMES_ARR)));
    /**
     * An array of mandatory field names
     */
    private static final String[] MANDATORY_FIELD_NAMES_ARR = MANDATORY_FIELDS.stream().map(EcmCsvField::getFieldName).toArray(String[]::new);

    /**
     * A set of all mandatory field names
     */
    public static final Set<String> MANDATORY_FIELD_NAMES = Collections
            .unmodifiableSet(new LinkedHashSet<>(Arrays.asList(MANDATORY_FIELD_NAMES_ARR)));

    private final String fieldName;
    private final MasterDataEntity masterDataEntity;

    /**
     * Create a new CSVField
     *
     * @param fieldName the field name (heading)
     */
    EcmCsvField(final String fieldName) {
        this(fieldName, null);
    }

    /**
     * Create a new CSVField
     *
     * @param fieldName        the field name (heading)
     * @param masterDataEntity optionally, the master data entity the field is resolved against
     */
    EcmCsvField(final String fieldName, final MasterDataEntity masterDataEntity) {
        this.fieldName = fieldName;
        this.masterDataEntity = masterDataEntity;
    }

    public static EcmCsvField forFieldName(final String name) {
        return Arrays.stream(values()).filter(f -> f.getFieldName().equals(name)).findFirst().orElse(null);
    }
}
