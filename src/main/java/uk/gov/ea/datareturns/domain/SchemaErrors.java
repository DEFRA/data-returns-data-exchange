package uk.gov.ea.datareturns.domain;

import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * @author adrianharrison
 * Records "selected" errors generated during Schema validation
 */
public class SchemaErrors {
	@JacksonXmlElementWrapper(useWrapping = false)
	private Map<String, LineError> lineErrors;

	public SchemaErrors()
	{
		this.lineErrors = new TreeMap<String, LineError>();
	}

	@JacksonXmlProperty(localName = "LineErrors")
	public Map<String, LineError> getLineErrors()
	{
		return lineErrors;
	}


	public void addLineErrror(LineError error) {
		this.lineErrors.put("Error_" + this.lineErrors.size(), error);
	}
	
	
	/** 
	 * Return total error count
	 * @return
	 */
	@JacksonXmlProperty(localName = "ErrorCount")
	public int getErrorCount()
	{
		return lineErrors.size();
	}

}