package uk.gov.ea.datareturns.domain.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Simple POJO containing the result of any data exchange action
 */
public class DataExchangeResult
{
	@JacksonXmlProperty(localName = "AppStatusCode")
	@JsonInclude(Include.NON_DEFAULT)
	private int appStatusCode;

	@JacksonXmlProperty(localName = "UploadResult")
	@JsonProperty("uploadResult")
	@JsonInclude(Include.NON_NULL)
	private UploadResult uploadResult;

	@JsonProperty("validationErrors")
	@JsonUnwrapped
	@JsonInclude(Include.NON_NULL)
	private ValidationErrors validationErrors;

	@JacksonXmlProperty(localName = "ParseResult")
	@JsonProperty("parseResult")
	@JsonInclude(Include.NON_NULL)
	private ParseResult parseResult;

	@JacksonXmlProperty(localName = "CompleteResult")
	@JsonProperty("completeResult")
	@JsonInclude(Include.NON_NULL)
	private CompleteResult completeResult;

	public DataExchangeResult()
	{
		// Set up a default value for the app status code.  The serializer will omit the appStatusCode
		// unless it is set to a different value
		this.appStatusCode = -1;
	}

	public DataExchangeResult(UploadResult uploadResult)
	{
		this();
		this.uploadResult = uploadResult;
	}

	public DataExchangeResult(CompleteResult completeResult)
	{
		this();
		this.completeResult = completeResult;
	}

	/**
	 * @return the appStatusCode
	 */
	public int getAppStatusCode() {
		return appStatusCode;
	}

	/**
	 * @param appStatusCode the appStatusCode to set
	 */
	public void setAppStatusCode(int appStatusCode) {
		this.appStatusCode = appStatusCode;
	}

	/**
	 * @return the uploadResult
	 */
	public UploadResult getUploadResult() {
		return uploadResult;
	}

	/**
	 * @param uploadResult the uploadResult to set
	 */
	public void setUploadResult(UploadResult uploadResult) {
		this.uploadResult = uploadResult;
	}

	/**
	 * @return the validationErrors
	 */
	public ValidationErrors getValidationErrors() {
		return validationErrors;
	}

	/**
	 * @param validationErrors the validationErrors to set
	 */
	public void setValidationErrors(ValidationErrors validationErrors) {
		this.validationErrors = validationErrors;
	}

	/**
	 * @return the parseResult
	 */
	public ParseResult getParseResult() {
		return parseResult;
	}

	/**
	 * @param parseResult the parseResult to set
	 */
	public void setParseResult(ParseResult parseResult) {
		this.parseResult = parseResult;
	}

	/**
	 * @return the completeResult
	 */
	public CompleteResult getCompleteResult() {
		return completeResult;
	}

	/**
	 * @param completeResult the completeResult to set
	 */
	public void setCompleteResult(CompleteResult completeResult) {
		this.completeResult = completeResult;
	}
}