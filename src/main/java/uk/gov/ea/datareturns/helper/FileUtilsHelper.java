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

import uk.gov.ea.datareturns.exception.system.DRFileReadException;
import uk.gov.ea.datareturns.exception.system.DRFileSaveException;

public abstract class FileUtilsHelper
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtilsHelper.class);
	
	/**
	 * Create full file path
	 * @param dir
	 * @param file
	 * @return
	 */
	public static String makeFullPath(String dir, String file)
	{
		return dir + File.separator + file;
	}

	/**
	 * Make full file name
	 * @param file
	 * @param ext
	 * @return
	 */
	public static String makeFileName(String file, String ext)
	{
		return file + "." + ext;
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

	/**
	 *  Make a CSV file name type
	 * @param file
	 * @return
	 */
	public static String makeCSVFileType(String file)
	{
		String fileType = FilenameUtils.getExtension(file);

		if (fileType == null || fileType.length() == 0)
		{
			return makeFileName(file, CSV.getFileType());
		} else
		{
			return makeFileName(file.substring(0, file.indexOf(fileType) - 1), CSV.getFileType());
		}
	}
	
	/**
	 * Create directory
	 * @param dir
	 * @throws IOException
	 */
	public static void createDirectory(String dir) throws IOException
	{
		LOGGER.debug("Creating directory '" + dir + "'");

		FileUtils.forceMkdir(new File(dir));

		LOGGER.debug("Directory created successfully");
	}
	
	/**
	 * Delete directory
	 * @param dir
	 * @throws IOException
	 */
	public static void deleteDirectory(String dir) throws IOException
	{
		LOGGER.debug("Deleting directory '" + dir + "'");
		
		FileUtils.deleteDirectory(new File(dir));

		LOGGER.debug("Directory deleted successfully");
	}

	/**
	 * Checks if file or directory exists
	 * @param fileLocation
	 * @return
	 */
	public static boolean fileOrDirectoryExists(String fileLocation)
	{
		LOGGER.debug("Checking if file '" + fileLocation + "' exists");

		boolean ret = new File(fileLocation).exists();

		LOGGER.debug("File does" + (ret ? "" : " not") + " exist");

		return ret;
	}
	
	/**
	 * Persist file stream to file location provided, directory is automatically created if required
	 * @param is
	 * @param filePath
	 * @return
	 */
	public static boolean saveFile(InputStream is, String filePath)
	{
		LOGGER.debug("Saving Input Stream to file '" + filePath + "'");

		try
		{
			FileUtils.copyInputStreamToFile(is, new File(filePath));
		} catch (IOException e)
		{
			throw new DRFileSaveException(e, "Unable to save file to '" + filePath + "'");
		}

		LOGGER.debug("File '" + filePath + "' saved successfully");

		return true;
	}

	/**
	 * Persist String contents to file
	 * @param contents
	 * @param fileOut
	 * @return
	 */
	public static boolean saveFile(String contents, String fileOut)
	{
		LOGGER.debug("Saving String to file '" + fileOut + "'");
		
		FileUtilsHelper.saveFile(new ByteArrayInputStream(contents.getBytes()), fileOut);

		LOGGER.debug("String saved to file successfully");

		return true;
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
	 * Read file contents to String
	 * @param filePath
	 * @return
	 */
	public static String loadFileAsString(String filePath)
	{
		LOGGER.debug("Loading file '" + filePath + "' to String");

		String content = null;

		try
		{
			content = new String(readAllBytes(get(filePath)));
		} catch (IOException e)
		{
			throw new DRFileReadException(e, "Unable to read from file '" + filePath + "'");
		}

		LOGGER.debug("File Loaded successfully");
		
		return content;
	}
}
