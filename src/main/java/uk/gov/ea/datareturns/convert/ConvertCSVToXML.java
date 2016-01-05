package uk.gov.ea.datareturns.convert;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.exception.application.InvalidContentsException;
import uk.gov.ea.datareturns.exception.application.NoReturnsException;
import uk.gov.ea.datareturns.exception.system.FileReadException;
import uk.gov.ea.datareturns.exception.system.FileSaveException;
import uk.gov.ea.datareturns.helper.FileUtilsHelper;
import fr.dralagen.Csv2xml;

public class ConvertCSVToXML extends ConvertFileToFile
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConvertCSVToXML.class);

	private final String ROOT_ELEMENT = "returns";
	private final String RETURN_ELEMENT = "return";
	private final String DELIMITER = ",";

	public ConvertCSVToXML()
	{
	}

	@Override
	public int convert()
	{
		Csv2xml converter = new Csv2xml();
		int rowsConverted = -1;

		String fileToConvert = getFileToConvert();
		String convertedFile = getXMLFileLocation();

		try
		{
			FileUtilsHelper.createDirectory(FilenameUtils.getPath(convertedFile));
		} catch (IOException e)
		{
			throw new FileSaveException(e, "Unable to save file to '" + convertedFile + "'");
		}

		LOGGER.debug("Converting file '" + fileToConvert + "' to '" + convertedFile + "'");

		converter.createNewDocument(ROOT_ELEMENT);

		try
		{
			InputStream csvInput = Csv2xml.getInputStream(fileToConvert);

			rowsConverted = converter.convert(csvInput, DELIMITER, RETURN_ELEMENT);

			OutputStream xmlOutput = new FileOutputStream(convertedFile);

			converter.writeTo(xmlOutput);

			xmlOutput.close();
			csvInput.close();

		} catch (FileNotFoundException e1)
		{
			throw new FileSaveException(e1, "Unable to save file to '" + convertedFile + "'");
		} catch (IOException e2)
		{
			throw new FileReadException(e2, "Unable to read from file '" + fileToConvert + "'");
		} catch (org.w3c.dom.DOMException e3)
		{
			throw new InvalidContentsException("Unable to read from file '" + fileToConvert + "'");
		}

		// File must contain at least 1 return
		if (rowsConverted == 0)
		{
			throw new NoReturnsException("No Returns found in file '" + fileToConvert + "'");
		}

		LOGGER.debug("File converted successfully for " + rowsConverted + " returns");

		setConvertedFile(convertedFile);

		return rowsConverted;
	}

	public String getConvertedFile()
	{
		return super.getConvertedFile();
	}

}
