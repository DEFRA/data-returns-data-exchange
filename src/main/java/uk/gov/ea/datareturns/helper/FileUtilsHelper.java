package uk.gov.ea.datareturns.helper;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static uk.gov.ea.datareturns.type.FileType.CSV;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.exception.system.FileReadException;
import uk.gov.ea.datareturns.exception.system.FileSaveException;

public class FileUtilsHelper
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtilsHelper.class);

	/**
	 * Persist String contents to file
	 * @param contents
	 * @param fileOut
	 * @return
	 */
	public static boolean saveStringToFile(String contents, String fileOut)
	{
		FileUtilsHelper.saveFile(new ByteArrayInputStream(contents.getBytes()), fileOut);

		return true;
	}

	/**
	 * Persist file stream to file location provided, directory is automatically created if required
	 * @param is
	 * @param filePath
	 * @return
	 */
	public static boolean saveFile(InputStream is, String filePath)
	{
		LOGGER.debug("Saving file '" + filePath + "'");

		try
		{
			FileUtils.copyInputStreamToFile(is, new File(filePath));
		} catch (IOException e)
		{
			throw new FileSaveException(e, "Unable to save file to '" + filePath + "'");
		}

		LOGGER.debug("File '" + filePath + "' saved successfully");

		return true;
	}

	/**
	 * Read file contents as String
	 * @param filePath
	 * @return
	 */
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

	/**
	 * Check file exists
	 * @param fileLocation
	 * @return
	 */
	public static boolean fileExists(String fileLocation)
	{
		File f = new File(fileLocation);

		LOGGER.debug("Checking if file '" + fileLocation + "' exists");

		LOGGER.debug("File does" + (f.exists() ? "" : " not") + " exist");

		return f.exists();
	}

	/**
	 * Creates full file path
	 * @param dir
	 * @param file
	 * @return
	 */
	public static String makeFullPath(String dir, String file)
	{
		return dir + File.separatorChar + file;
	}

	// TODO should really handle case where file extensional ready exists file name?
	/**
	 * Create file name with .csv extension
	 * @param file
	 * @return
	 */
	public static String makeCSVFileType(String file)
	{
		String fileType = FilenameUtils.getExtension(file);

		if (fileType == null)
		{
			return makeFileType(file, CSV.getFileType());
		} else
		{
			return makeFileType(file.substring(0, file.indexOf(fileType) - 1), CSV.getFileType());
		}
	}

	/**
	 * Make full file name
	 * @param file
	 * @param fileType
	 * @return
	 */
	public static String makeFileType(String file, String fileType)
	{
		return file + "." + fileType;
	}

	/**
	 * Delete file
	 * @param filePath
	 * @throws IOException
	 */
	public static void deleteFile(String filePath) throws IOException
	{
		LOGGER.debug("Deleting file '" + filePath + "'");

		FileUtils.forceDelete(new File(filePath));

		LOGGER.debug("File deleted successfully");
	}

	/**
	 * Delete directory
	 * @param dir
	 * @throws IOException
	 */
	public static void deleteDirectory(String dir) throws IOException
	{
		FileUtils.deleteDirectory(new File(dir));
	}

	/**
	 * Create directory
	 * @param dir
	 * @throws IOException
	 */
	public static void createDirectory(String dir) throws IOException
	{
		FileUtils.forceMkdir(new File(dir));
	}
}
