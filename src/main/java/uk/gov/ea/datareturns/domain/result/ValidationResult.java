package uk.gov.ea.datareturns.domain.result;

import uk.gov.ea.datareturns.domain.SchemaErrors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ValidationResult
{
	@JacksonXmlProperty(localName = "SchemaErrors")
	@JacksonXmlElementWrapper(useWrapping = false)
	private SchemaErrors schemaErrors;

	public SchemaErrors getSchemaErrors()
	{
		return this.schemaErrors;
	}

	public void setSchemaErrors(SchemaErrors schemaErrors)
	{
		this.schemaErrors = schemaErrors;
	}
	
	@JsonIgnore
	public boolean isValid()
	{
		return (this.schemaErrors.getErrorCount() == 0);
	}
}
