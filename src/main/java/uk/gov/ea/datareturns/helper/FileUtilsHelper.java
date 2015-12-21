package uk.gov.ea.datareturns.helper;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.exception.system.FileReadException;
import uk.gov.ea.datareturns.exception.system.FileSaveException;
import uk.gov.ea.datareturns.exception.system.FileUnlocatableException;

public class FileUtilsHelper
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtilsHelper.class);

	/**
	 * Persist file stream to file location provided
	 * @param filePath
	 * @return
	 */
	public static boolean saveFile(InputStream is, String filePath)
	{
		boolean ret = false;
		String fileName = FilenameUtils.getName(filePath);
		File file = new File(filePath);

		LOGGER.debug("Saving file '" + filePath + "'");

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

		LOGGER.debug("File saved successfully");

		return ret;
	}

	public static String loadFileAsString(String filePath)
	{
		String content = null;
		
		try
		{
			content = new String(readAllBytes(get(filePath)));
		} catch (IOException e)
		{
			throw new FileReadException(e, "Unable to read from file '" + filePath + "'");
		}

		return content;
	}

	// TODO needs to be refactored to enable full testing
	/**
	 * Verify file contains minimum rows expected.
	 * The count includes Header row and cannot guarantee they are complete
	 * @param filePath
	 * @param i
	 * @return
	 */
	public static boolean fileContainsMinRows(String filePath, int minRows)
	{
		File file = new File(filePath);
		String fileName = FilenameUtils.getName(filePath);
		int lineNo = 1;
		boolean minRowsFound = false;

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			@SuppressWarnings("unused")
			String line;

			while ((line = br.readLine()) != null)
			{
				if (lineNo++ == minRows)
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

	public static boolean fileExists(String fileLocation)
	{
		File f = new File(fileLocation);

		LOGGER.debug("Checking if file '" + fileLocation + "' exists");

		LOGGER.debug("File does " + (f.exists() ? "" : "not") + " exist");

		return f.exists();
	}

	public static String makeFullPath(String dir, String file)
	{
		return dir + File.separatorChar + file;
	}
	
	public static void deleteDirectory(String dir)
	{
		try
		{
			FileUtils.deleteDirectory(new File(dir));

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void createDirectory(String dir)
	{

		try
		{
			FileUtils.forceMkdir(new File(dir));
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
