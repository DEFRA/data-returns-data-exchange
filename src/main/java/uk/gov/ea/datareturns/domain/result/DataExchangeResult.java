package uk.gov.ea.datareturns.domain.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Simple POJO containing the result of any data exchange action
 */
public class DataExchangeResult
{
	@JacksonXmlProperty(localName = "AppStatusCode")
	private int appStatusCode;

	@JacksonXmlProperty(localName = "UploadResult")
	@JsonProperty("uploadResult")
	@JsonInclude(Include.NON_NULL)
	private UploadResult uploadResult;

	@JacksonXmlProperty(localName = "ValidationResult")
	@JsonProperty("validationResult")
	@JsonInclude(Include.NON_NULL)
	private ValidationResult validationResult;

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
	}

	public DataExchangeResult(UploadResult uploadResult)
	{
		this.uploadResult = uploadResult;
	}

	public DataExchangeResult(CompleteResult completeResult)
	{
		this.completeResult = completeResult;
	}

	/**
	 * @return the appStatusCode
	 */
	public int getAppStatusCode() {
		return appStatusCode;
	}

	/**
	 * @return the uploadResult
	 */
	public UploadResult getUploadResult() {
		return uploadResult;
	}

	/**
	 * @return the validationResult
	 */
	public ValidationResult getValidationResult() {
		return validationResult;
	}

	/**
	 * @return the parseResult
	 */
	public ParseResult getParseResult() {
		return parseResult;
	}

	/**
	 * @return the completeResult
	 */
	public CompleteResult getCompleteResult() {
		return completeResult;
	}

	/**
	 * @param appStatusCode the appStatusCode to set
	 */
	public void setAppStatusCode(int appStatusCode) {
		this.appStatusCode = appStatusCode;
	}

	/**
	 * @param uploadResult the uploadResult to set
	 */
	public void setUploadResult(UploadResult uploadResult) {
		this.uploadResult = uploadResult;
	}

	/**
	 * @param validationResult the validationResult to set
	 */
	public void setValidationResult(ValidationResult validationResult) {
		this.validationResult = validationResult;
	}

	/**
	 * @param parseResult the parseResult to set
	 */
	public void setParseResult(ParseResult parseResult) {
		this.parseResult = parseResult;
	}

	/**
	 * @param completeResult the completeResult to set
	 */
	public void setCompleteResult(CompleteResult completeResult) {
		this.completeResult = completeResult;
	}
}