package uk.gov.ea.datareturns.helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.exception.system.DRFileSaveException;

public abstract class FileUtilsHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtilsHelper.class);

	/**
	 * Create full file path
	 * 
	 * @param dir
	 * @param file
	 * @return
	 */
	public static String makeFullPath(String dir, String file) {
		return dir + File.separator + file;
	}

	/**
	 * Persist file stream to file location provided, directory is automatically
	 * created if required
	 * 
	 * @param is
	 * @param file
	 * @return
	 */
	public static boolean saveFile(InputStream is, File file) {
		LOGGER.debug("Saving Input Stream to file '" + file.getAbsolutePath() + "'");
		try {
			FileUtils.copyInputStreamToFile(is, file);
		} catch (IOException e) {
			throw new DRFileSaveException(e, "Unable to save file to '" + file.getAbsolutePath() + "'");
		}

		LOGGER.debug("File '" + file.getAbsolutePath() + "' saved successfully");

		return true;
	}

	/**
	 * Delete file
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	public static void deleteFile(String filePath) throws IOException {
		LOGGER.debug("Deleting file '" + filePath + "'");

		FileUtils.forceDelete(new File(filePath));

		LOGGER.debug("File deleted successfully");
	}
}
