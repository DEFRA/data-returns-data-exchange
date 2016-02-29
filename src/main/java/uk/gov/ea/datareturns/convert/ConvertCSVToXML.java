package uk.gov.ea.datareturns.convert;

import static uk.gov.ea.datareturns.helper.FileUtilsHelper.makeFullPath;
import static uk.gov.ea.datareturns.type.FileType.CSV;
import static uk.gov.ea.datareturns.type.FileType.XML;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;

import uk.gov.ea.datareturns.exception.application.DRInvalidContentsException;
import uk.gov.ea.datareturns.exception.application.DRNoReturnsException;
import uk.gov.ea.datareturns.exception.system.DRFileReadException;
import uk.gov.ea.datareturns.exception.system.DRFileSaveException;
import uk.gov.ea.datareturns.helper.FileUtilsHelper;
import fr.dralagen.Csv2xml;

public class ConvertCSVToXML
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConvertCSVToXML.class);

	private final String ROOT_ELEMENT = "returns";
	private final String ROW_ELEMENT = "return";

	private String separator;
	private String fileToConvert;
	private String outputLocation;
	private String convertedFile;

	public ConvertCSVToXML(String separator, String fileToConvert, String outputLocation)
	{
		this.separator = separator;
		this.fileToConvert = fileToConvert;
		this.outputLocation = outputLocation;

		this.convertedFile = getXMLFileLocation();
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

	public String getConvertedFile()
	{
		return convertedFile;
	}

	/**
	 * Changes csv extension to xml
	 * @return
	 */
	protected String getXMLFileLocation()
	{
		String fileNameIn = new File(fileToConvert).getName();
		String fileNameOut = fileNameIn.replaceAll(CSV.getFileType(), XML.getFileType());

		return makeFullPath(outputLocation, fileNameOut);
	}

	public int convert()
	{
		Csv2xml converter = new Csv2xml();
		int rowsConverted = -1;

		try
		{
			FileUtilsHelper.createDirectory(FilenameUtils.getPath(convertedFile));
		} catch (IOException e)
		{
			throw new DRFileSaveException(e, "Unable to save file to '" + convertedFile + "'");
		}

		LOGGER.debug("Converting file '" + fileToConvert + "' to '" + convertedFile + "'");

		converter.createNewDocument(ROOT_ELEMENT);

		try (
				InputStream csvInput = Csv2xml.getInputStream(fileToConvert);
				OutputStream xmlOutput = new FileOutputStream(convertedFile);
			)
		{
			rowsConverted = converter.convert(csvInput, separator, ROW_ELEMENT);
			converter.writeTo(xmlOutput);

		} catch (FileNotFoundException e1)
		{
			throw new DRFileSaveException(e1, "Unable to save file to '" + convertedFile + "'");
		} catch (IOException e2)
		{
			throw new DRFileReadException(e2, "Unable to read from file '" + fileToConvert + "'");
		} catch (DOMException e3)
		{
			throw new DRInvalidContentsException("Invalid contents found in file '" + fileToConvert + "'");
		}

		// File must contain at least 1 return
		if (rowsConverted == 0)
		{
			throw new DRNoReturnsException("No Returns found in file '" + fileToConvert + "'");
		}

		LOGGER.debug("File converted successfully for " + rowsConverted + " returns");

		return rowsConverted;
	}
}
