package uk.gov.ea.datareturns.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

// TODO trim all Strings coming in
/**
 * Simple POJO to contain values passed in/out of service
 * Srrings will never be null
 */
public class DataExchangeResult
{
	private int appStatusCode;
	private String fileName;
	private String fileKey;
	private String eaId;
	private String siteName;
	private String returnType;
	private String message;
	private String userEmail;
	private List<DataExchangeError> errors;

	public DataExchangeResult()
	{
		this.fileName = "";
		this.fileKey = "";
		this.eaId = "";
		this.siteName = "";
		this.returnType = "";
		this.message = "";
		this.userEmail = "";
		this.errors = new ArrayList<DataExchangeError>();
	}

	public DataExchangeResult(String fileKey, String userEmail)
	{
		this();
		this.fileKey = fileKey;
		this.userEmail = userEmail;
	}

	public DataExchangeResult(String key, String eaId, String siteName, String returnType)
	{
		this();
		this.fileKey = key;
		this.eaId = eaId;
		this.siteName = siteName;
		this.returnType = returnType;
	}

	public int getAppStatusCode()
	{
		return appStatusCode;
	}

	public void setAppStatusCode(int appStatusCode)
	{
		this.appStatusCode = appStatusCode;
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

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getUserEmail()
	{
		return userEmail;
	}

	public void setUserEmail(String userEmail)
	{
		this.userEmail = userEmail;
	}

	public List<DataExchangeError> getErrors()
	{
		return errors;
	}

	public void setErrors(List<DataExchangeError> errors)
	{
		this.errors = errors;
	}

	public DataExchangeError addError(String error)
	{
		DataExchangeError err = new DataExchangeError(error);

		this.errors.add(err);

		return err;
	}
	
	@JsonIgnore
	public boolean isSendUserEmail()
	{
		return !"".equals(userEmail.trim());
	}
}