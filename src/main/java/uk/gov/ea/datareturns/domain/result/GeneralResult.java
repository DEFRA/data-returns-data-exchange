package uk.gov.ea.datareturns.domain.result;

import java.util.Map;
import java.util.Map.Entry;

import uk.gov.ea.datareturns.domain.Result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * @author adrianharrison
 * Holds "General" purpose type objects i.e. the result of an XSL transformation
 */
public class GeneralResult
{
	@JacksonXmlProperty(localName = "TransformationResults")
	@JacksonXmlElementWrapper(useWrapping = false)
	private TransformationResult transformationResults;

	public TransformationResult getTransformationResults()
	{
		return transformationResults;
	}

	public void setTransformationResults(TransformationResult transformationResults)
	{
		this.transformationResults = transformationResults;
	}

	@JsonIgnore
	public String getSingleResultValue()
	{
		if (transformationResults.getResults() != null) {
			Map.Entry<String, Result> entry = transformationResults.getResults().entrySet().iterator().next();
			Result result = entry.getValue();
			
			return result.getValue();
		}
		return null;
	}

	@JsonIgnore
	public Result getResultById(String Id)
	{
		if (transformationResults.getResults() != null) {
			Map<String, Result> results = transformationResults.getResults();
			Entry<String, Result> result = results.entrySet().stream().filter(r -> r.getValue().getId().contains(Id)).findFirst().orElse(null);
			
			return result.getValue();
		}
		return null;
	}

	/** 
	 * Return total error count
	 * @return
	 */
	@JacksonXmlProperty(localName = "ResultCount")
	public int getResultCount()
	{
		if (transformationResults.getResults() != null) {
			return transformationResults.getResults().size();
		}
		return 0;
	}
}
