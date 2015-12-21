package uk.gov.ea.datareturns.convert;

import static uk.gov.ea.datareturns.helper.CommonHelper.makeFullFilePath;
import static uk.gov.ea.datareturns.type.FileType.CSV;
import static uk.gov.ea.datareturns.type.FileType.XML;

import java.io.File;

public abstract class ConvertFileToFile extends ConvertBase
{
	private String fileToConvert;
	private String outputLocation;
	private String convertedFile;

	@Override
	public abstract int convert();
	
	public String getFileToConvert()
	{
		return fileToConvert;
	}

	public void setFileToConvert(String fileToConvert)
	{
		this.fileToConvert = fileToConvert;
	}

	public String getOutputLocation()
	{
		return outputLocation;
	}

	public void setOutputLocation(String outputLocation)
	{
		this.outputLocation = outputLocation;
	}

	public String getConvertedFile()
	{
		return convertedFile;
	}

	public void setConvertedFile(String convertedFile)
	{
		this.convertedFile = convertedFile;
	}

	/**
	 * Changes csv extension to xml
	 * @return
	 */
	protected String getXMLFileLocation()
	{
		String fileNameIn = new File(fileToConvert).getName();
		String fileNameOut = fileNameIn.replaceAll(CSV.getFileType(), XML.getFileType());

		return makeFullFilePath(outputLocation, fileNameOut);
	}
}
