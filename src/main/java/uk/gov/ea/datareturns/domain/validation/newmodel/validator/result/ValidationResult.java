package uk.gov.ea.datareturns.domain.validation.newmodel.validator.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.ControlledListEntity;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.AbstractAliasingEntityValue;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.AbstractEntityValue;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.FieldValue;

import javax.validation.ConstraintViolation;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Graham Willis - simplified validation result
 */
public class ValidationResult {
    /** Pattern for extracting errorCode and errorType from hibernate validation message template */
    private static final Pattern ERROR_KEY_PATTERN = Pattern.compile("^\\{DR(?<errorCode>\\d{4})-(?<errorType>\\w+)}$");

    public class ErrorValue {
        @JsonProperty("inputValue")
        private final String inputValue;

        @JsonProperty("entityName")
        private final String entityName;

        public ErrorValue(String inputValue, String entityName) {
            this.inputValue = inputValue;
            this.entityName = entityName;
        }
        public ErrorValue(String inputValue) {
            this.inputValue = inputValue;
            this.entityName = null;
        }
    }

    @JsonPropertyOrder(value = { "errorCode", "errorType", "errorMessage", "errorValues" })
    public class ValidationError {

        @JsonProperty("errorCode")
        private int errorCode;

        @JsonProperty("errorType")
        private String errorType;

        @JsonProperty("errorMessage")
        private String errorMessage;

        @JsonProperty("errorValues")
        private List<ErrorValue> errorValueList = new ArrayList<>();

        public int getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorType() {
            return errorType;
        }

        public void setErrorType(String errorType) {
            this.errorType = errorType;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        // Record the details from the field value
        public void add(FieldValue<?> fieldValue) {
            if (fieldValue != null) {

                ErrorValue ev;
                Optional<String> entityName = Optional.empty();

                if (AbstractAliasingEntityValue.class.isInstance(fieldValue)) {
                    AbstractAliasingEntityValue abstractAliasingEntityValue = (AbstractAliasingEntityValue)fieldValue;
                   if (abstractAliasingEntityValue.getEntity() != null) {
                        entityName = Optional.ofNullable(abstractAliasingEntityValue.getEntity().getName());
                   }
                } else if (AbstractEntityValue.class.isInstance(fieldValue)) {
                    AbstractEntityValue abstractEntityValue = (AbstractEntityValue)fieldValue;
                    if (abstractEntityValue.getEntity() != null) {
                        entityName = Optional.ofNullable(abstractEntityValue.getEntity().getName());
                    }
                }

                if (entityName.isPresent()) {
                    ev = new ErrorValue(fieldValue.getInputValue(), entityName.get());
                } else {
                    ev = new ErrorValue(fieldValue.getInputValue());
                }

                this.errorValueList.add(ev);
            }
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("errors")
    private final Map<String, ValidationError> errors = new HashMap<>();

    @JsonProperty("valid")
    public boolean isValid() {
        return this.errors.isEmpty();
    }

    public ValidationError forViolation(ConstraintViolation<?> violation) {
        int errorCode = 0;
        String errorType = "Unknown";
        final Matcher errorKeyMatcher = ERROR_KEY_PATTERN.matcher(violation.getMessageTemplate());
        if (errorKeyMatcher.matches()) {
            errorCode = Integer.parseInt(errorKeyMatcher.group("errorCode"));
            errorType = errorKeyMatcher.group("errorType");
        }

        final String mapKey = toMapKey(errorCode, errorType);
        ValidationError error = errors.get(mapKey);

        if (error == null) {
            error = new ValidationError();
            error.setErrorCode(errorCode);
            error.setErrorType(errorType);
            error.setErrorMessage(violation.getMessage());
            errors.put(mapKey, error);
        }
        return error;
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

    public Map<String, ValidationError> getErrors() {
        return errors;
    }
}
