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

	@JacksonXmlProperty(localName = "GeneralResult")
	@JsonProperty("generalResult")
	@JsonInclude(Include.NON_NULL)
	private GeneralResult generalResult;

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

	public int getAppStatusCode()
	{
		return appStatusCode;
	}

	public void setAppStatusCode(int appStatusCode)
	{
		this.appStatusCode = appStatusCode;
	}

	public UploadResult getUploadResult()
	{
		return uploadResult;
	}

	public void setUploadResult(UploadResult uploadResult)
	{
		this.uploadResult = uploadResult;
	}

	public ValidationResult getValidationResult()
	{
		return validationResult;
	}

	public void setValidationResult(ValidationResult validationResult)
	{
		this.validationResult = validationResult;
	}

	public GeneralResult getGeneralResult()
	{
		return generalResult;
	}

	public void setGeneralResult(GeneralResult generalResult)
	{
		this.generalResult = generalResult;
	}

	public CompleteResult getCompleteResult()
	{
		return completeResult;
	}

	public void setCompleteResult(CompleteResult completeResult)
	{
		this.completeResult = completeResult;
	}
}