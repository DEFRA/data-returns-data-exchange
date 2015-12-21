package uk.gov.ea.datareturns.domain.result;

import java.util.LinkedHashMap;
import java.util.Map;

import uk.gov.ea.datareturns.domain.Result;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class TransformationResult
{
	@JacksonXmlProperty(localName = "Results")
	private Map<String, Result> results = new LinkedHashMap<String, Result>();

	public Map<String, Result> getResults()
	{
		return results;
	}

	public void setResults(Map<String, Result> results)
	{
		this.results = results;
	}
}
