package uk.gov.ea.datareturns.resources;

import java.util.ArrayList;
import java.util.List;

public class UploadResult
{
	String outcome;
	String key;
	List<UploadError> errors = new ArrayList<UploadError>();

	public UploadResult()
	{
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

	public void addError(String error)
	{
		this.errors.add(new UploadError(error));
	}
}