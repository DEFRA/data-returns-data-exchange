package uk.gov.ea.datareturns.helper;

import java.io.File;

public class CommonHelper
{
	public static String makeFullPath(String leftPart, String rightPart)
	{
		return leftPart + File.separator + rightPart;
	}

	public static String getFileType(String filePath)
	{
		String fileType = null;
		int i = filePath.lastIndexOf('.');

		if (i > 0)
		{
			fileType = filePath.substring(i + 1).toLowerCase();
		}

		return fileType;
	}
}
