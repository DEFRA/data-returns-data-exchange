package uk.gov.defra.datareturns.service.csv;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Provides definitions for all fields defined in the DEP
 *
 * @author Sam Gardner-Dell
 */
public enum CSVField {
    /**
     * The EA Unique Identifier (EA_ID)
     */
    EA_ID("EA_ID"),
    /**
     * The site name (Site_Name)
     */
    Site_Name("Site_Name"),

    /**
     * The return type (Rtn_Type)
     */
    Rtn_Type("Rtn_Type"),

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
    Parameter("Parameter"),

    /**
     * Qualifier (Qualifier)
     */
    Qualifier("Qualifier"),

    /**
     * Value (Value)
     */
    Value("Value"),

    /**
     * Textual value (Txt_Value)
     */
    Txt_Value("Txt_Value"),

    /**
     * Unit of measurement (Unit)
     */
    Unit("Unit"),

    /**
     * Reference period
     */
    Ref_Period("Ref_Period"),

    /**
     * Method or standard used (Meth_Stand)
     */
    Meth_Stand("Meth_Stand"),

    /**
     * Record comments (Comments)
     */
    Comments("Comments"),

    /**
     * Commercial in confidence data (CiC)
     */
    CiC("CiC"),

    /**
     * Releases and transfers
     */
    Rel_Trans("Rel_Trans");


    /**
     * An array of all field names
     */
    public static final String[] ALL_FIELD_NAMES_ARR = Arrays.stream(values()).map(CSVField::getName)
            .toArray(String[]::new);

    /**
     * A set of all field names
     */
    public static final Set<String> ALL_FIELD_NAMES = Collections.unmodifiableSet(
            new LinkedHashSet<>(Arrays.asList(ALL_FIELD_NAMES_ARR)));

    /**
     * The set of fields which must always be present in the source data
     */
    public static final Set<CSVField> MANDATORY_FIELDS = Collections.unmodifiableSet(new LinkedHashSet<>(
            Arrays.asList(EA_ID, Site_Name, Rtn_Type, Mon_Date, Mon_Point, Parameter)));

    /**
     * An array of mandatory field names
     */
    public static final String[] MANDATORY_FIELD_NAMES_ARR = MANDATORY_FIELDS.stream().map(CSVField::getName)
            .toArray(String[]::new);

    /**
     * A set of all mandatory field names
     */
    public static final Set<String> MANDATORY_FIELD_NAMES = Collections.unmodifiableSet(
            new LinkedHashSet<>(Arrays.asList(MANDATORY_FIELD_NAMES_ARR)));

    private final String name;

    /**
     * Create a new CSVField
     *
     * @param name the field name (heading)
     */
    <T> CSVField(final String name) {
        this.name = name;
    }

    public static CSVField forFieldName(final String fieldName) {
        return Arrays.stream(values()).filter(f -> f.getName().equals(fieldName)).findFirst().orElse(null);
    }

    public String getName() {
        return name;
    }
}
