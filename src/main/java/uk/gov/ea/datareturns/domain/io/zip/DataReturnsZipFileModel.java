/**
 *
 */
package uk.gov.ea.datareturns.domain.io.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

/**
 * Handles reading and writing returns data to/from zip files
 *
 * @author Sam Gardner-Dell
 */
public class DataReturnsZipFileModel {
	/** The directory within the zip file where the input file is stored (this is the original file that was uploaded by the user */
	private static final String DIR_INPUT = "/input/";
	/** The directory within the zip file where the output files are stored (one output file is stored per permit number encountered */
	private static final String DIR_OUTPUT = "/output/";

	private File inputFile;

	private Collection<File> outputFiles;

	/**
	 *
	 */
	public DataReturnsZipFileModel() {

	}

	/**
	 * @return the inputFile
	 */
	public File getInputFile() {
		return this.inputFile;
	}

	/**
	 * @param inputFile the inputFile to set
	 */
	public void setInputFile(final File inputFile) {
		this.inputFile = inputFile;
	}

	/**
	 * @return the outputFiles
	 */
	public Collection<File> getOutputFiles() {
		return this.outputFiles;
	}

	/**
	 * @param outputFiles the outputFiles to set
	 */
	public void setOutputFiles(final Collection<File> outputFiles) {
		this.outputFiles = outputFiles;
	}

	/**
	 * Add a new outputfile to the list of output files
	 *
	 * @param outputFile the {@link File} to be added
	 */
	public void addOutputFile(final File outputFile) {
		if (this.outputFiles == null) {
			this.outputFiles = new ArrayList<>();
		}
		this.outputFiles.add(outputFile);
	}

	public final File toZipFile(final File workFolder) throws IOException {
		final File zipFile = new File(workFolder, this.inputFile.getName() + ".zip");
		try (OutputStream fos = FileUtils.openOutputStream(zipFile);
				ZipOutputStream zos = new ZipOutputStream(fos);) {
			zos.setMethod(ZipOutputStream.DEFLATED);
			zos.setLevel(6);

			ZipEntry entry = new ZipEntry(DIR_INPUT + this.inputFile.getName());
			zos.putNextEntry(entry);
			FileUtils.copyFile(this.inputFile, zos);
			zos.closeEntry();

			for (final File file : this.outputFiles) {
				entry = new ZipEntry(DIR_OUTPUT + file.getName());
				zos.putNextEntry(entry);
				FileUtils.copyFile(file, zos);
				zos.closeEntry();
			}
		}
		return zipFile;
	}

	public static final DataReturnsZipFileModel fromZipFile(final File workFolder, final File zipFileObj) throws IOException {
		final DataReturnsZipFileModel zipFileModel = new DataReturnsZipFileModel();

		try (ZipFile zipFile = new ZipFile(zipFileObj)) {
			final Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				final ZipEntry entry = entries.nextElement();

				try (InputStream is = zipFile.getInputStream(entry)) {
					final File tempFile = new File(workFolder, entry.getName());
					FileUtils.copyInputStreamToFile(is, tempFile);

					if (entry.getName().startsWith(DIR_INPUT)) {
						zipFileModel.setInputFile(tempFile);
					} else {
						zipFileModel.addOutputFile(tempFile);
					}
				}
			}
		}
		return zipFileModel;
	}

}
