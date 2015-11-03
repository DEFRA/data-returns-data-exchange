package uk.gov.ea.datareturns.helper;

import java.io.File;

public class CommonHelper
{
	/**
	 * Returns a full OS independent file path
	 * @param leftPart
	 * @param rightPart
	 * @return
	 */
	public static String makeFullPath(String leftPart, String rightPart)
	{
		return leftPart + File.separator + rightPart;
	}

	/**
	 * Extracts and returns the file extension (in lower case) from the supplied file name 
	 * @param filePath
	 * @return
	 */
	public static String getFileType(String filePath)
	{
		String fileType = null;
		int i = filePath.lastIndexOf('.');

		if (i > 0)
		{
			fileType = filePath.substring(i + 1).toLowerCase();
		}
		
		// TODO maybe throw an exception if null?

		return fileType;
	}
}
