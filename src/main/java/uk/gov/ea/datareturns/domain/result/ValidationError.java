package uk.gov.ea.datareturns.domain.result;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an individual validation error (constraint violation) which occurred when validating the model
 *
 * @author Sam Gardner-Dell
 */
@SuppressWarnings("unused")
public class ValidationError {

    @JsonProperty("definition")
    private String definition;

    @JsonProperty("lineNumber")
    private int lineNumber;

    @JsonProperty("errorType")
    private String errorType;

    @JsonProperty("errorCode")
    private int errorCode;

    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonProperty("errorData")
    private ErrorData[] errorData;

    /**
     * Used to transmit the error value (the user input value),
     * and the resolved value (where there is an input that is mutated, for instanced aliases,
     * but where the resolved value is still invalid (for dependencies for example),
     * for each field name required by the front end for error reporting
     */
    @SuppressWarnings("unused")
    public static class ErrorData {
        @JsonProperty("fieldName")
        private String fieldName;

        @JsonProperty("errorValue")
        private String errorValue;

        @JsonProperty("resolvedValue")
        private String resolvedValue;

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getErrorValue() {
            return errorValue;
        }

        public void setErrorValue(String errorValue) {
            this.errorValue = errorValue;
        }

        public String getResolvedValue() {
            return resolvedValue;
        }

        public void setResolvedValue(String resolvedValue) {
            this.resolvedValue = resolvedValue;
        }
    }

    /**
     * Create a new ValidationError instance
     */
    public ValidationError() {
    }


    public int getRecordIndex() {
        return this.getLineNumber() - 2;
    }

    public void setRecordIndex(int index) {
        this.setLineNumber(index + 2);
    }

    /**
     * @return the definition
     */
    public String getDefinition() {
        return this.definition;
    }

    /**
     * @param definition the definition to set
     */
    public void setDefinition(final String definition) {
        this.definition = definition;
    }

    /**
     * @return the lineNumber
     */
    public int getLineNumber() {
        return this.lineNumber;
    }

    /**
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(final int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @return the errorType
     */
    public String getErrorType() {
        return this.errorType;
    }

    /**
     * @param errorType the errorType to set
     */
    public void setErrorType(final String errorType) {
        this.errorType = errorType;
    }

    /**
     * @return the errorCode
     */
    public int getErrorCode() {
        return this.errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorData[] getErrorData() {
        return errorData;
    }

    public void setErrorData(ErrorData[] errorData) {
        this.errorData = errorData;
    }

}