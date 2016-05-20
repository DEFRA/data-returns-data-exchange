/**
 *
 */
package uk.gov.ea.datareturns.domain.processors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.gov.ea.datareturns.config.MiscSettings;
import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;

/**
 * @author Sam Gardner-Dell
 * @param <R> the result object returned by the processor
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AbstractReturnsProcessor<R> {
	private final MiscSettings miscSettings;

	protected final File workingFolder;

	/**
	 *
	 *
	 * @param miscSettings configuration settings.
	 * @throws ProcessingException
	 */
	public AbstractReturnsProcessor(final MiscSettings miscSettings) throws ProcessingException {
		this.miscSettings = miscSettings;
		this.workingFolder = createWorkFolder();
	}

	/**
	 * Process the data.
	 *
	 * @return the result of the process as defined by the underlying concrete implementation
	 * @throws ProcessingException if an error occurs during processing.
	 */
	public final R process() throws ProcessingException {
		try {
			return doProcess();
		} finally {
			cleanup();
		}
	}

	/**
	 * Internal doProcess method to be implemented by concrete subclasses
	 *
	 * @return
	 * @throws ProcessingException
	 */
	protected abstract R doProcess() throws ProcessingException;

	protected void cleanup() throws ProcessingException {

	}

	/**
	 * Create a work folder for the session
	 *
	 * @return a {@link File} pointing to a working folder
	 */
	private File createWorkFolder() throws ProcessingException {
		try {
			final java.nio.file.Path outputPath = new File(this.miscSettings.getOutputLocation()).toPath().normalize();
			if (!Files.exists(outputPath, LinkOption.NOFOLLOW_LINKS)) {
				Files.createDirectories(outputPath);
			}
			return Files.createTempDirectory(outputPath, "dr").toFile();
		} catch (final IOException e) {
			throw new ProcessingException("Unable to create working folder", e);
		}
	}
}
