package uk.gov.ea.datareturns.factory;

import uk.gov.ea.datareturns.convert.ConvertCSVToXML;
import uk.gov.ea.datareturns.convert.ConvertFileToFile;
import uk.gov.ea.datareturns.exception.application.UnsupportedFileTypeException;

public class ConvertFactory
{
	private static final String FILE_TYPE_CSV = "csv";

	public static ConvertFileToFile getConverter(String type)
	{
		ConvertFileToFile converter = null;

		switch (type.toLowerCase())
		{
			case FILE_TYPE_CSV:
				converter = new ConvertCSVToXML();
				break;
			default:
				throw new UnsupportedFileTypeException("Unsupported file type '" + type + "'");
		}

		return converter;
	}
}
