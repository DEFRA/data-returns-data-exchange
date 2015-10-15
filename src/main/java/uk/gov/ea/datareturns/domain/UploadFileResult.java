package uk.gov.ea.datareturns.domain;

import java.util.ArrayList;
import java.util.List;

public class UploadFileResult
{
	// TODO remove redundant properties
	private int appStatusCode;
	private String message;;

	private String outcome;
	private String outcomeMessage;;
	private String fileName;

	private String fileKey;
	private String eaId;
	private String siteName;
	private String returnType;
	private List<UploadFileError> errors;

	public UploadFileResult()
	{
		this.fileKey = "";
		this.outcome = "";
		this.outcomeMessage ="";
		this.fileName = "";
		this.eaId = "";
		this.siteName = "";
		this.returnType = "";
		this.errors = new ArrayList<UploadFileError>();
	}

	public UploadFileResult(String key, String eaId, String siteName, String returnType)
	{
		this.fileKey = key;
		this.eaId = eaId;
		this.siteName = siteName;
		this.returnType = returnType;
		this.errors = new ArrayList<UploadFileError>();
	}
	
	public int getAppStatusCode()
	{
		return appStatusCode;
	}

	public void setAppStatusCode(int appStatusCode)
	{
		this.appStatusCode = appStatusCode;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
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

	public List<UploadFileError> getErrors()
	{
		return errors;
	}

	public void setErrors(List<UploadFileError> errors)
	{
		this.errors = errors;
	}

	public UploadFileError addError(String error)
	{
		UploadFileError err = new UploadFileError(error);

		this.errors.add(err);

		return err;
	}

	public void addErrors(List<UploadFileError> errors)
	{
		this.errors.addAll(errors);
	}

	public void addError(String reason, String lineNo, String columnName, String errValue)
	{
		this.errors.add(new UploadFileError(reason, lineNo, columnName, errValue));
	}

}