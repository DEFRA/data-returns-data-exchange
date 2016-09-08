package uk.gov.ea.datareturns.domain.result;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an individual validation error (constraint violation) which occurred when validating the model
 *
 * @author Sam Gardner-Dell
 */
public class ValidationError {
    @JsonProperty("fieldName")
    private String fieldName;

    @JsonProperty("definition")
    private String definition;

    @JsonProperty("helpReference")
    private String helpReference;

    @JsonProperty("lineNumber")
    private long lineNumber;

    @JsonProperty("errorType")
    private String errorType;

    @JsonProperty("errorCode")
    private int errorCode;

    @JsonProperty("errorValue")
    private String errorValue;

    @JsonProperty("errorMessage")
    private String errorMessage;

    /**
     * Create a new ValidationError instance
     */
    public ValidationError() {
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * @param fieldName the fieldName to set
     */
    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
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
     * @return the helpReference
     */
    public String getHelpReference() {
        return this.helpReference;
    }

    /**
     * @param helpReference the helpReference to set
     */
    public void setHelpReference(final String helpReference) {
        this.helpReference = helpReference;
    }

    /**
     * @return the lineNumber
     */
    public long getLineNumber() {
        return this.lineNumber;
    }

    /**
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(final long lineNumber) {
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
     * @return the errorValue
     */
    public String getErrorValue() {
        return this.errorValue;
    }

    /**
     * @param errorValue the errorValue to set
     */
    public void setErrorValue(final String errorValue) {
        this.errorValue = errorValue;
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

    @Override
    public String toString() {
        return "ValidationError{" +
                "fieldName='" + fieldName + '\'' +
                ", definition='" + definition + '\'' +
                ", lineNumber=" + lineNumber +
                ", errorType='" + errorType + '\'' +
                ", errorCode=" + errorCode +
                ", errorValue='" + errorValue + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}