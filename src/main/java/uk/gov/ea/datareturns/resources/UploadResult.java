package uk.gov.ea.datareturns.resources;

import java.util.ArrayList;
import java.util.List;

public class UploadResult
{
	String returnType;
	String outcome;
	String key;
	List<UploadError> errors = new ArrayList<UploadError>();

	public UploadResult()
	{
	}

	public String getReturnType()
	{
		return returnType;
	}

	public void setReturnType(String returnType)
	{
		this.returnType = returnType;
	}

	public String getOutcome()
	{
		return outcome;
	}

	public void setOutcome(String outcome)
	{
		this.outcome = outcome;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
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