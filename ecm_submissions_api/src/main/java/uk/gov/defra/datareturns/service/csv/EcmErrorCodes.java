package uk.gov.defra.datareturns.service.csv;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The intersection between the error codes DR...and associated fields
 * <p>
 * This class allows the message codes to be associated with a group of fields so that each
 * unique error message can be contextualized by other data on the row.
 * <p>
 * This gets rid of the problem whereby the hibernate validator error cannot
 * carry a complex payload.
 * <p>
 * The String constant is stored in the hibernate violation messageTemplate - the
 * DataSampleValidator reads this from the violation and determines the set of EcmCsvFields
 * for to retrieve from the record and to serialize on returned error message.
 */
public final class EcmErrorCodes {


    private static final Map<String, List<EcmCsvField>> VALIDATION_FIELD_MAP = new HashMap<>();
    private static final Pattern TEMPLATE_CHARS = Pattern.compile("[{}]");

    static {
        /*
         * Add the (atomic) length errors to the map
         */
        VALIDATION_FIELD_MAP.put(Length.CIC, Collections.singletonList(EcmCsvField.CiC));
        VALIDATION_FIELD_MAP.put(Length.COMMENTS, Collections.singletonList(EcmCsvField.Comments));
        VALIDATION_FIELD_MAP.put(Length.MON_POINT, Collections.singletonList(EcmCsvField.Mon_Point));
        /*
         * VALIDATION_FIELD_MAP.put in the (atomic) Incorrect errors to the map
         */
        VALIDATION_FIELD_MAP.put(Incorrect.MON_POINT, Collections.singletonList(EcmCsvField.Mon_Point));
        VALIDATION_FIELD_MAP.put(Incorrect.VALUE, Collections.singletonList(EcmCsvField.Value));
        VALIDATION_FIELD_MAP.put(Incorrect.MON_DATE, Collections.singletonList(EcmCsvField.Mon_Date));
        VALIDATION_FIELD_MAP.put(Incorrect.METH_STAND, Collections.singletonList(EcmCsvField.Meth_Stand));
        VALIDATION_FIELD_MAP.put(Incorrect.EA_ID, Collections.singletonList(EcmCsvField.EA_ID));
        VALIDATION_FIELD_MAP.put(Incorrect.PARAMETER, Collections.singletonList(EcmCsvField.Parameter));
        VALIDATION_FIELD_MAP.put(Incorrect.QUALIFIER, Collections.singletonList(EcmCsvField.Qualifier));
        VALIDATION_FIELD_MAP.put(Incorrect.REF_PERIOD, Collections.singletonList(EcmCsvField.Ref_Period));
        VALIDATION_FIELD_MAP.put(Incorrect.RTN_PERIOD, Collections.singletonList(EcmCsvField.Rtn_Period));
        VALIDATION_FIELD_MAP.put(Incorrect.RTN_TYPE, Collections.singletonList(EcmCsvField.Rtn_Type));
        VALIDATION_FIELD_MAP.put(Incorrect.TXT_VALUE, Collections.singletonList(EcmCsvField.Txt_Value));
        VALIDATION_FIELD_MAP.put(Incorrect.UNIT, Collections.singletonList(EcmCsvField.Unit));
        /*
         * VALIDATION_FIELD_MAP.put the (atomic) missing errors to map
         */
        VALIDATION_FIELD_MAP.put(Missing.UNIT, Collections.singletonList(EcmCsvField.Unit));
        VALIDATION_FIELD_MAP.put(Missing.EA_ID, Collections.singletonList(EcmCsvField.EA_ID));
        VALIDATION_FIELD_MAP.put(Missing.MON_POINT, Collections.singletonList(EcmCsvField.Mon_Point));
        VALIDATION_FIELD_MAP.put(Missing.PARAMETER, Collections.singletonList(EcmCsvField.Parameter));
        VALIDATION_FIELD_MAP.put(Missing.RTN_TYPE, Collections.singletonList(EcmCsvField.Rtn_Type));
        VALIDATION_FIELD_MAP.put(Missing.SITE_NAME, Collections.singletonList(EcmCsvField.Site_Name));
        VALIDATION_FIELD_MAP.put(Missing.MON_DATE, Collections.singletonList(EcmCsvField.Mon_Date));
        /*
         * VALIDATION_FIELD_MAP.put in the conflicts, optionally missing and dependency validations etc. The convention being applied is to
         * VALIDATION_FIELD_MAP.put first the primary data item - the header the error is being reported on and then
         * items in descending order of relevance.
         */
        VALIDATION_FIELD_MAP.put(Conflict.PROHIBIT_UNIT_FOR_TXT_VALUE, Collections.singletonList(EcmCsvField.Unit));
        VALIDATION_FIELD_MAP.put(Missing.REQUIRE_COMMENTS_FOR_TXT_VALUE, Collections.singletonList(EcmCsvField.Comments));
        VALIDATION_FIELD_MAP.put(Conflict.PROHIBIT_TEXT_VALUE_WITH_NUMERIC_VALUE, Arrays.asList(EcmCsvField.Value, EcmCsvField.Txt_Value));
        VALIDATION_FIELD_MAP.put(Missing.REQUIRE_VALUE_OR_TXT_VALUE, Arrays.asList(EcmCsvField.Value, EcmCsvField.Txt_Value));
        VALIDATION_FIELD_MAP.put(Conflict.UNIQUE_IDENTIFIER_SITE_CONFLICT, Arrays.asList(EcmCsvField.EA_ID, EcmCsvField.Site_Name));
    }

    /**
     * private constructor
     */
    private EcmErrorCodes() {

    }

    public static List<EcmCsvField> getFieldDependencies(final String message) {
        return VALIDATION_FIELD_MAP.get(message);
    }

    public static String toErrorCode(final String template) {
        return TEMPLATE_CHARS.matcher(template).replaceAll("");
    }

    /**
     * The errors categorized as length errors
     */
    public static final class Length {
        public static final String CIC = "{DR9150-Length}";
        public static final String COMMENTS = "{DR9140-Length}";
        public static final String MON_POINT = "{DR9060-Length}";
    }

    /**
     * The errors categorized as Incorrect errors
     */
    public static final class Incorrect {
        public static final String MON_POINT = "{DR9060-Incorrect}";
        public static final String VALUE = "{DR9040-Incorrect}";
        public static final String MON_DATE = "{DR9020-Incorrect}";
        public static final String METH_STAND = "{DR9100-Incorrect}";
        public static final String EA_ID = "{DR9000-Incorrect}";
        public static final String PARAMETER = "{DR9030-Incorrect}";
        public static final String QUALIFIER = "{DR9180-Incorrect}";
        public static final String REF_PERIOD = "{DR9090-Incorrect}";
        public static final String RTN_PERIOD = "{DR9070-Incorrect}";
        public static final String RTN_TYPE = "{DR9010-Incorrect}";
        public static final String TXT_VALUE = "{DR9080-Incorrect}";
        public static final String UNIT = "{DR9050-Incorrect}";
    }

    /**
     * The errors categorized as missing errors
     */
    public static final class Missing {
        public static final String UNIT = "{DR9050-Missing}";
        public static final String EA_ID = "{DR9000-Missing}";
        public static final String MON_POINT = "{DR9060-Missing}";
        public static final String PARAMETER = "{DR9030-Missing}";
        public static final String RTN_TYPE = "{DR9010-Missing}";
        public static final String SITE_NAME = "{DR9110-Missing}";
        public static final String REQUIRE_COMMENTS_FOR_TXT_VALUE = "{DR9140-Missing}";
        public static final String REQUIRE_VALUE_OR_TXT_VALUE = "{DR9999-Missing}";
        public static final String MON_DATE = "{DR9020-Missing}";
    }

    /**
     * The errors categorized as conflict errors
     */
    public static final class Conflict {
        public static final String PROHIBIT_UNIT_FOR_TXT_VALUE = "{DR9050-Conflict}";
        public static final String PROHIBIT_TEXT_VALUE_WITH_NUMERIC_VALUE = "{DR9999-Conflict}";
        public static final String UNIQUE_IDENTIFIER_SITE_CONFLICT = "{DR9110-Conflict}";
    }
}
