package uk.gov.ea.datareturns.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;

import uk.gov.ea.datareturns.exception.system.FileReadException;
import uk.gov.ea.datareturns.exception.system.FileSaveException;
import uk.gov.ea.datareturns.exception.system.FileUnlocatableException;

// TODO some of this should/could be farmed off to FileUtils
public class FileUtilsHelper
{
	/**
	 * Persist file stream to file location provided
	 * @param uploadedInputStream
	 * @param filePath
	 */
	public static void saveReturnsFile(InputStream uploadedInputStream, String filePath)
	{
		String fileName = FilenameUtils.getName(filePath);
		
		int read;
		final int BUFFER_LENGTH = 1024;
		final byte[] buffer = new byte[BUFFER_LENGTH];

		try
		{
			OutputStream out = new FileOutputStream(new File(filePath));

			while ((read = uploadedInputStream.read(buffer)) != -1)
			{
				out.write(buffer, 0, read);
			}

			out.flush();
			out.close();
		} catch (FileNotFoundException e1)
		{
			throw new FileSaveException("Unable to save file to '" + fileName + "'");
		} catch (IOException e2)
		{
			throw new FileReadException("Unable to read from file '" + fileName + "'");
		}
	}
	
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
		FileReader fr;
		int lineNo = 1;
		boolean minRowsFound = false;

		try
		{
			fr = new FileReader(file);

			BufferedReader br = new BufferedReader(fr);
			String line = null;
			
			while ((line = br.readLine()) != null)
			{
				if (lineNo++ == 2)
				{
					minRowsFound = true;

					break;
				}
			}

			br.close();
			fr.close();

		} catch (FileNotFoundException e1)
		{
			throw new FileUnlocatableException("Cannot locate file '" + fileName + "'");
		} catch (IOException e2)
		{
			throw new FileReadException("Unable to read from file '" + fileName + "'");
		}

		return minRowsFound;
	}
}
