package uk.gov.ea.datareturns.domain.result;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Container for validation error detail found in the validated model
 *
 * @author Sam Gardner-Dell
 */
public class ValidationErrors {
    /** Pattern for extracting errorCode and errorType from hibernate validation message template */
    private static final Pattern ERROR_KEY_PATTERN = Pattern.compile("^\\{DR(?<errorCode>\\d{4})-(?<errorType>\\w+)}$");

    @JsonIgnore
    private final Map<String, ValidationErrorType> errors = new HashMap<>();

    /**
     * Check if a validation error exists
     *
     * @return true if a validation error exists, false otherwise
     */
    @JsonIgnore
    public boolean isValid() {
        return this.errors.isEmpty();
    }

    /**
     * @return the list of distinct validation errors which occurred
     */
    @JsonGetter("validationErrors")
    public List<ValidationErrorType> getErrorList() {
        return new ArrayList<>(errors.values());
    }

    /**
     * Sets the list of distinct validation errors which occurred.
     *
     * @param errorList the list of distinct validation errors which occurred
     */
    @JsonSetter("validationErrors")
    public void setErrorList(List<ValidationErrorType> errorList) {
        this.errors.clear();
        errorList.forEach(e -> this.errors.put(toMapKey(e.getErrorCode(), e.getErrorType()), e));
    }

    /**
     * Flattens the error hierarchy down to provide a list for every single error on every single row.
     *
     * @return a flattened list of errors
     */
    public List<FlatView> flatView() {
        List<FlatView> theList = new ArrayList<>();
        for (ValidationErrorType type : errors.values()) {
            for (ValidationErrorInstance instance : type.getInstances()) {
                for (Integer rowIndex : instance.getRecordIndices()) {
                    theList.add(new FlatView(rowIndex, type, instance));
                }
            }
        }
        return theList;
    }

    /**
     * Retrieve the {@link ValidationErrorType} for a given error code and error type
     *
     * @param errorCode the error code
     * @param errorType the error type
     * @return the {@link ValidationErrorType} for the given code/type parameters, or null if not found
     */
    public ValidationErrorType forError(int errorCode, String errorType) {
        return errors.get(toMapKey(errorCode, errorType));
    }

    /**
     * Retrieve {@link ValidationErrorType} for the validation engine {@link ConstraintViolation}
     *
     * If necessary a new {@link ValidationErrorType} will be created (and stored).
     *
     * @param violation the violation
     * @return the {@link ValidationErrorType} for the given {@link ConstraintViolation}
     */
    public ValidationErrorType forViolation(ConstraintViolation<?> violation) {
        int errorCode = 0;
        String errorType = "Unknown";
        final Matcher errorKeyMatcher = ERROR_KEY_PATTERN.matcher(violation.getMessageTemplate());
        if (errorKeyMatcher.matches()) {
            errorCode = Integer.parseInt(errorKeyMatcher.group("errorCode"));
            errorType = errorKeyMatcher.group("errorType");
        }

        final String mapKey = toMapKey(errorCode, errorType);
        ValidationErrorType type = errors.get(mapKey);

        if (type == null) {
            type = new ValidationErrorType();
            type.setErrorCode(errorCode);
            type.setErrorType(errorType);
            type.setErrorMessage(violation.getMessage());
            errors.put(mapKey, type);
        }
        return type;
    }

    /**
     * Generate a map key from the given error code and error type
     * @param errorCode the error code
     * @param errorType the error type
     * @return a map key which uniquely identifies the error code and error type
     */
    private static String toMapKey(int errorCode, String errorType) {
        return errorCode + "-" + errorType;
    }

    /**
     * Used to flatten the error class hierarchy down to associate errors on specific row indexes with with the {@link ValidationErrorType}
     * and {@link ValidationErrorInstance} to which they belong
     */
    public static class FlatView {
        private final Integer recordIndex;
        private final ValidationErrorType type;
        private final ValidationErrorInstance instance;

        /**
         * Instantiates a new Flat view.
         *
         * @param recordIndex the record index
         * @param type the type
         * @param instance the instance
         */
        FlatView(Integer recordIndex, ValidationErrorType type,
                ValidationErrorInstance instance) {
            this.recordIndex = recordIndex;
            this.type = type;
            this.instance = instance;
        }

        /**
         * Gets record index.
         *
         * @return the record index
         */
        public Integer getRecordIndex() {
            return recordIndex;
        }

        /**
         * Gets type.
         *
         * @return the type
         */
        public ValidationErrorType getType() {
            return type;
        }

        /**
         * Gets instance.
         *
         * @return the instance
         */
        public ValidationErrorInstance getInstance() {
            return instance;
        }
    }
}