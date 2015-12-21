package uk.gov.ea.datareturns.domain;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SchemaErrorHandler implements ErrorHandler
{
	private SchemaErrors schemaErrors;

	public SchemaErrorHandler()
	{
		this.schemaErrors = new SchemaErrors();
	}

	public SchemaErrors getSchemaErrors()
	{
		return schemaErrors;
	}

	public void warning(SAXParseException ex)
	{
		schemaErrors.addLineError(ex.getLineNumber(), "warning", ex.getMessage());
	}

	public void error(SAXParseException ex)
	{
		schemaErrors.addLineError(ex.getLineNumber(), "error", ex.getMessage());
	}

	public void fatalError(SAXParseException ex) throws SAXException
	{
		schemaErrors.addLineError(ex.getLineNumber(), "fatal", ex.getMessage());
	}
}
