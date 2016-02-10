package uk.gov.ea.datareturns.convert;

import static uk.gov.ea.datareturns.helper.XMLUtilsHelper.transformToString;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.helper.FileUtilsHelper;

public class ConvertXMLToCSV
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConvertXMLToCSV.class);

	private String separator;
	private String fileToConvert;
	private String outputLocation;
	private String xslt;

	public ConvertXMLToCSV(String separator, String fileToConvert, String outputLocation, String xslt)
	{
		this.separator = separator;
		this.fileToConvert = fileToConvert;
		this.outputLocation = outputLocation;
		this.xslt = xslt;
	}

	public String getSeparator()
	{
		return separator;
	}

	public String getFileToConvert()
	{
		return fileToConvert;
	}

	public String getOutputLocation()
	{
		return outputLocation;
	}

	public String getXslt()
	{
		return xslt;
	}

	public String convert()
	{
		String convertedFile = FileUtilsHelper.makeCSVFileType(fileToConvert);

		LOGGER.debug("Converting file '" + fileToConvert + "' to '" + convertedFile + "'");

		Map<String, String> params = new HashMap<String, String>();
		params.put("separator", separator);

		String contents = transformToString(fileToConvert, xslt, params);

		LOGGER.debug("contents = " + contents);
		
		LOGGER.debug("File '" + fileToConvert + "' converted successfully");

		FileUtilsHelper.saveStringToFile(contents, convertedFile);

		return convertedFile;
	}
}
