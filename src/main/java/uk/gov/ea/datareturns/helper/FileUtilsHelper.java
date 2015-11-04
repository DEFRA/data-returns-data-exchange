package uk.gov.ea.datareturns.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FilenameUtils;

import uk.gov.ea.datareturns.exception.system.FileReadException;
import uk.gov.ea.datareturns.exception.system.FileSaveException;
import uk.gov.ea.datareturns.exception.system.FileUnlocatableException;

public class FileUtilsHelper
{
	/**
	 * Persist file stream to file location provided
	 * @param uploadedInputStream
	 * @param filePath
	 */
	public static boolean saveReturnsFile(InputStream is, String fullFilePath)
	{
		boolean ret = false;
		String fileName = FilenameUtils.getName(fullFilePath);
		File file = new File(fullFilePath);

		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			String line;
			
			while ((line = br.readLine()) != null)
			{
				bw.write(line);
				bw.newLine();
			}

			bw.flush();
			bw.close();
			br.close();

			ret = true;
		} catch (FileNotFoundException e1)
		{
			throw new FileSaveException(e1, "Unable to save file to '" + fileName + "'");
		} catch (IOException e2)
		{
			throw new FileReadException(e2, "Unable to read from file '" + fileName + "'");
		}

		return ret;
	}

	// TODO needs to be refactored to enable testing fully
	/**
	 * Verifys if file contains minimum rows expected (currently 2) 
	 * @param filePath
	 * @param i
	 * @return
	 */
	public static boolean fileContainsMinRows(String filePath, int i)
	{
		File file = new File(filePath);
		String fileName = FilenameUtils.getName(filePath);
		int lineNo = 1;
		boolean minRowsFound = false;

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null)
			{
				if (lineNo++ == 2)
				{
					minRowsFound = true;

					break;
				}
			}

			br.close();

		} catch (FileNotFoundException e1)
		{
			throw new FileUnlocatableException(e1, "Cannot locate file '" + fileName + "'");
		} catch (IOException e2)
		{
			throw new FileReadException(e2, "Unable to read from file '" + fileName + "'");
		}

		return minRowsFound;
	}
}
