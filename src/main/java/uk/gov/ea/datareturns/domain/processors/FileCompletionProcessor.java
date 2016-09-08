package uk.gov.ea.datareturns.domain.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.config.ProcessorSettings;
import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;
import uk.gov.ea.datareturns.domain.io.zip.DataReturnsZipFileModel;
import uk.gov.ea.datareturns.domain.model.EaId;
import uk.gov.ea.datareturns.domain.monitorpro.MonitorProTransportHandler;
import uk.gov.ea.datareturns.domain.result.CompleteResult;
import uk.gov.ea.datareturns.domain.result.DataExchangeResult;
import uk.gov.ea.datareturns.domain.storage.StorageProvider;
import uk.gov.ea.datareturns.domain.storage.StorageProvider.StoredFile;
import uk.gov.ea.datareturns.util.StopWatch;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Processor for submitting previously validated files to MonitorPro.
 * This processor retrieves previously uploaded and validated files (via the {@link FileUploadProcessor}) using the unique file key
 * returned by that processor.
 *
 * @author Sam Gardner-Dell
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileCompletionProcessor extends AbstractReturnsProcessor<DataExchangeResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileCompletionProcessor.class);

    private final MonitorProTransportHandler monitorProHandler;

    private final StorageProvider storage;

    private String userEmail;

    private String storedFileKey;

    private String originalFilename;

    /**
     * Create a new {@link FileCompletionProcessor}.
     *
     * The file completion processor handles the submission of previously validated files to MonitorPro
     *
     * @param processorSettings processor specific configuration settings
     * @param storage the storage provider.
     * @param monitorProHandler the MonitorPro transport handler
     * @throws ProcessingException if a processing error occurs while attempting to complete the file submission
     */
    @Inject
    public FileCompletionProcessor(final ProcessorSettings processorSettings, final StorageProvider storage,
            final MonitorProTransportHandler monitorProHandler) throws ProcessingException {
        super(processorSettings);
        this.storage = storage;
        this.monitorProHandler = monitorProHandler;
    }

    /* (non-Javadoc)
     * @see uk.gov.ea.datareturns.domain.processors.AbstractReturnsProcessor#doProcess()
     */
    @Override
    public DataExchangeResult doProcess() throws ProcessingException {
        try {
            final StopWatch stopwatch = new StopWatch("data-exchange complete timer");
            stopwatch.startTask("Retrieving file from temporary storage");

            final StoredFile storedFile = this.storage.retrieveTemporaryData(this.storedFileKey);

            stopwatch.startTask("Extracting data files");
            final DataReturnsZipFileModel zipModel = DataReturnsZipFileModel.fromZipFile(this.workingFolder, storedFile.getFile());

            stopwatch.startTask("Sending data to monitor pro");
            for (final File outputFile : zipModel.getOutputFiles()) {
                final EaId eaId = zipModel.getOutputFileIdentifiers().get(outputFile.getName());
                this.monitorProHandler.sendNotifications(eaId, outputFile);
            }

            stopwatch.startTask("Moving data to audit store");
            final Map<String, String> metadata = new LinkedHashMap<>();
            metadata.put("originator-email", this.userEmail);
            metadata.put("original-filename", this.originalFilename);

            this.storage.moveToAuditStore(this.storedFileKey, metadata);

            stopwatch.stop();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(stopwatch.prettyPrint());
            }

            return new DataExchangeResult(new CompleteResult(this.storedFileKey, this.userEmail));
        } catch (final IOException e) {
            throw new ProcessingException(e);
        }
    }

    /**
     * @param userEmail the userEmail to set
     */
    public void setUserEmail(final String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * @param storedFileKey the storedFileKey to set
     */
    public void setStoredFileKey(final String storedFileKey) {
        this.storedFileKey = storedFileKey;
    }

    /**
     * @param originalFilename the originalFilename to set
     */
    public void setOriginalFilename(final String originalFilename) {
        this.originalFilename = originalFilename;
    }
}
