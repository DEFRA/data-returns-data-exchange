package uk.gov.ea.datareturns.domain.result;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidationError {
	@JsonProperty("fieldName")
	private String fieldName;

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

	
	public ValidationError() {
		
	}


	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}


	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}


	/**
	 * @return the lineNumber
	 */
	public long getLineNumber() {
		return lineNumber;
	}


	/**
	 * @param lineNumber the lineNumber to set
	 */
	public void setLineNumber(long lineNumber) {
		this.lineNumber = lineNumber;
	}


	/**
	 * @return the errorType
	 */
	public String getErrorType() {
		return errorType;
	}


	/**
	 * @param errorType the errorType to set
	 */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}


	/**
	 * @return the errorCode
	 */
	public int getErrorCode() {
		return errorCode;
	}


	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}


	/**
	 * @return the errorValue
	 */
	public String getErrorValue() {
		return errorValue;
	}


	/**
	 * @param errorValue the errorValue to set
	 */
	public void setErrorValue(String errorValue) {
		this.errorValue = errorValue;
	}


	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}


	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}