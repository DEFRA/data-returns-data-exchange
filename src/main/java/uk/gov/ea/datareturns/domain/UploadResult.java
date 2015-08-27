package uk.gov.ea.datareturns.domain;

import java.util.ArrayList;
import java.util.List;

public class UploadResult
{
	private String outcome;
	private String outcomeMessage;;
	private String fileName;
	private String fileKey;
	private String eaId;
	private String siteName;
	private String returnType;
	private List<UploadError> errors;

	public UploadResult()
	{
		this.fileKey = "";
		this.outcome = "";
		this.outcomeMessage ="";
		this.fileName = "";
		this.eaId = "";
		this.siteName = "";
		this.returnType = "";
		this.errors = new ArrayList<UploadError>();
	}

	public UploadResult(String key, String eaId, String siteName, String returnType)
	{
		this.fileKey = key;
		this.eaId = eaId;
		this.siteName = siteName;
		this.returnType = returnType;
		this.errors = new ArrayList<UploadError>();
	}
	
	public String getOutcome()
	{
		return outcome;
	}

	public void setOutcome(String outcome)
	{
		this.outcome = outcome;
	}

	public String getOutcomeMessage()
	{
		return outcomeMessage;
	}

	public void setOutcomeMessage(String outcomeMessage)
	{
		this.outcomeMessage = outcomeMessage;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getFileKey()
	{
		return fileKey;
	}

	public void setFileKey(String fileKey)
	{
		this.fileKey = fileKey;
	}

	public String getEaId()
	{
		return eaId;
	}

	public void setEaId(String eaId)
	{
		this.eaId = eaId;
	}

	public String getSiteName()
	{
		return siteName;
	}

	public void setSiteName(String siteName)
	{
		this.siteName = siteName;
	}

	public String getReturnType()
	{
		return returnType;
	}

	public void setReturnType(String returnType)
	{
		this.returnType = returnType;
	}

	public List<UploadError> getErrors()
	{
		return errors;
	}

	public void setErrors(List<UploadError> errors)
	{
		this.errors = errors;
	}

	public UploadError addError(String error)
	{
		UploadError err = new UploadError(error);

		this.errors.add(err);

		return err;
	}

	public void addErrors(List<UploadError> errors)
	{
		this.errors.addAll(errors);
	}

	public void addError(String reason, String lineNo, String columnName, String errValue)
	{
		this.errors.add(new UploadError(reason, lineNo, columnName, errValue));
	}

}