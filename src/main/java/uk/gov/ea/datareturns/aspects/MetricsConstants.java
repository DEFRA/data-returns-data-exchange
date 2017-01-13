package uk.gov.ea.datareturns.aspects;

/**
 * Constants used for metrics collection
 *
 * @author Sam Gardner-Dell
 */
public class MetricsConstants {

    /**
     * Constants common to various metrics types
     */
    public interface Common {
        /** measurement (table) name for validation events */
        String MEASUREMENT_VALIDATION_EVENT = "validation_event";
        /** measurement (table) name for validation errors */
        String MEASUREMENT_VALIDATION_ERROR = "validation_error";
        /** measurement (table) name for controlled list usage data */
        String MEASUREMENT_CONTROLLED_LIST_USAGE = "controlled_list_usage";

        /** tag for host reporting metrics */
        String TAG_HOST = "host";
        /** tag for EA_ID related data */
        String TAG_EA_ID = "ea_id";
    }

    /**
     * Constants specific to Controlled List Usage metrics
     */
    public interface ControlledListUsage extends Common {
        /** the controlled list that was used */
        String TAG_CONTROLLED_LIST = "controlled_list";
        /** the preferred name of the item used, always present */
        String TAG_ITEM_NAME = "item_name";
        /** the alias that was used (if populated, then an alias was used) */
        String TAG_ITEM_ALIAS = "item_alias";
        /** the usage type (preferred or alias) */
        String TAG_USAGE_TYPE = "usage_type";

        /** number of times the controlled list entry was used */
        String FIELD_USAGE_COUNT = "usage_count";
    }

    /**
     * Constants specific to Validation Error metrics
     */
    public interface ValidationError extends Common {
        /** the error (error code and error type */
        String TAG_ERROR = "error";
        /** the field(s) which caused the error */
        String TAG_ERROR_FIELD = "error_field";
        /** the error type (missing, incorrect, etc) */
        String TAG_ERROR_TYPE = "error_type";

        /** the error count */
        String FIELD_ERROR_COUNT = "error_count";
    }

    /**
     * Constants specific to Validation Event metrics
     */
    public interface ValidationEvent extends Common {
        /** the status of the validation */
        String TAG_VALIDATION_STATUS = "validation_status";

        /** the total number of records processed */
        String FIELD_RECORD_COUNT = "record_count";
        /** the total number of errors found */
        String FIELD_TOTAL_ERROR_COUNT = "total_error_count";
        /** the total number of unique errors found */
        String FIELD_UNIQUE_ERROR_COUNT = "unique_error_count";
        /** the total number of return types processed */
        String FIELD_RETURN_TYPE_COUNT = "rtn_type_count";
        /** the total number of EA_ID's processed */
        String FIELD_EA_ID_COUNT = "eaid_count";
        /** time taken (in ms) to process the file */
        String FIELD_RUNTIME_MS = "runtime_ms";
    }

}
