/**
 *
 */
package uk.gov.ea.datareturns.domain.processors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.gov.ea.datareturns.config.ProcessorSettings;
import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;

/**
 * Abstract base class for data returns service request processors.
 *
 * @author Sam Gardner-Dell
 * @param <R> the result object returned by the processor
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AbstractReturnsProcessor<R> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReturnsProcessor.class);
	private final ProcessorSettings processorSettings;
	/** the working folder to be used by, and unique to, this processor instance */
	protected final File workingFolder;

	/**
	 * Constructor for concrete implementations of the AbstractReturnsProcessor
	 *
	 * @param processorSettings configuration settings.
	 * @throws ProcessingException if an error occurs setting up the processor.
	 */
	public AbstractReturnsProcessor(final ProcessorSettings processorSettings) throws ProcessingException {
		this.processorSettings = processorSettings;
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
	 * @return the result of the process
	 * @throws ProcessingException if an error occurred while attempting to execute the processor
	 */
	protected abstract R doProcess() throws ProcessingException;

	/**
	 * Clean up tasks to be run after the processor has completed.
	 *
	 * Note: This is an override point.  Overriding implementations must call super.cleanup()
	 */
	protected void cleanup() {
		if (!FileUtils.deleteQuietly(workingFolder)) {
			try {
				FileUtils.forceDeleteOnExit(workingFolder);
			} catch (final IOException e) {
				LOGGER.error("Unable to cleanup after processor " + this.getClass().getName(), e);
			}
		}
	}

	/**
	 * Create a work folder for the session
	 *
	 * @return a {@link File} pointing to a working folder
	 * @throws ProcessingException if a problem occurred attempting to create the work folder
	 */
	private File createWorkFolder() throws ProcessingException {
		try {
			final java.nio.file.Path outputPath = new File(this.processorSettings.getOutputLocation()).toPath().normalize();
			if (!Files.exists(outputPath, LinkOption.NOFOLLOW_LINKS)) {
				Files.createDirectories(outputPath);
			}
			return Files.createTempDirectory(outputPath, "dr").toFile();
		} catch (final IOException e) {
			throw new ProcessingException("Unable to create working folder", e);
		}
	}

	/**
	 * @return the processorSettings
	 */
	protected ProcessorSettings getProcessorSettings() {
		return this.processorSettings;
	}
}
