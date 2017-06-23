package uk.gov.ea.datareturns.domain.monitorpro;

import com.samskivert.mustache.Template;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.config.email.MonitorProEmailConfiguration;
import uk.gov.ea.datareturns.domain.model.rules.EaIdType;
import uk.gov.ea.datareturns.util.MustacheTemplates;

import javax.inject.Inject;
import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the transport of data to MonitorPro
 *
 * Currently this is performed via email (for lack of a more elegant means to send data to MonitorPro)
 *
 * @author Sam Gardner-Dell
 */
@Component
public class MonitorProTransportHandler implements TransportHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorProTransportHandler.class);

    private static final String MONITOR_PRO_MUSTACHE_TEMPLATE_KEY = "MONITOR_PRO_TEMPLATE";

    private final MonitorProEmailConfiguration settings;

    /**
     * Create a new MonitorProTransportHandler
     *
     * @param settings the configuration settings for MonitorPro
     */
    @Inject
    public MonitorProTransportHandler(final MonitorProEmailConfiguration settings) {
        this.settings = settings;
    }

    /**
     * Send a notification to MonitorPro for the given CSV file
     *
     * @param originatorEmail the email address of the user that uploaded the source file that this output file has been created from
     * @param originatorFilename the name of the file the user uploaded to the datareturns service
     * @param eaId the EA Unique Identifier that the data in the output file pertains to
     * @param fileKey the AWS S3 file key that contains the returnsCSVFile
     * @param returnsCSVFile the CSV file to send to MonitorPro
     * @throws MonitorProTransportException if a problem occurred when attempting to send the file to MonitorPro
     */
    @Override public void sendNotifications(final String originatorEmail, final String originatorFilename, final String eaId,
            final String fileKey, final File returnsCSVFile)
            throws MonitorProTransportException {
        sendNotifications(originatorEmail, originatorFilename, eaId, fileKey, returnsCSVFile, new DefaultTransportHandler());
    }

    /**
     * Send a notification to MonitorPro for the given CSV file using the specified transport handler
     *
     * @param originatorEmail the email address of the user that uploaded the source file that this output file has been created from
     * @param originatorFilename the name of the file the user uploaded to the datareturns service
     * @param eaId the EA Unique Identifier that the data in the output file pertains to
     * @param fileKey the AWS S3 file key that contains the returnsCSVFile
     * @param returnsCSVFile the CSV file to send to MonitorPro
     * @param handler the transport handler to use to submit the file to MonitorPro
     * @throws MonitorProTransportException if a problem occurred when attempting to send the file to MonitorPro
     */
    public void sendNotifications(final String originatorEmail, final String originatorFilename, final String eaId,
            final String fileKey, final File returnsCSVFile,
            final EmailTransportHandler handler)
            throws MonitorProTransportException {
        LOGGER.debug("Sending Email with attachment '" + returnsCSVFile.getAbsolutePath() + "'");
        try {
            final MultiPartEmail email = new MultiPartEmail();
            final String subject = this.settings.getDatabaseName(EaIdType.forUniqueId(eaId));

            email.setHostName(this.settings.getHost());
            email.setSmtpPort(this.settings.getPort());

            email.setSubject(subject);
            email.addTo(this.settings.getTo());
            email.setFrom(this.settings.getFrom());

            final Map<String, String> mustacheTemplateData = new HashMap<>();
            mustacheTemplateData.put("EA_ID", eaId);
            mustacheTemplateData.put("originatorEmail", originatorEmail);
            mustacheTemplateData.put("originatorFilename", originatorFilename);
            mustacheTemplateData.put("currentDate", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));

            Template template = MustacheTemplates.get(MONITOR_PRO_MUSTACHE_TEMPLATE_KEY, this.settings.getBody());

            final String messageBody = template.execute(mustacheTemplateData);
            email.setMsg(messageBody);
            email.setStartTLSEnabled(this.settings.isUseTLS());

            final EmailAttachment attachment = new EmailAttachment();
            attachment.setDisposition(EmailAttachment.ATTACHMENT);
            attachment.setDescription("Data Returns File Upload");

            String attachmentName = "DR_"
                    + FilenameUtils.getBaseName(fileKey)
                    + "_"
                    + FilenameUtils.getBaseName(returnsCSVFile.getName())
                    + ".csv";
            attachment.setName(attachmentName);
            attachment.setPath(returnsCSVFile.getAbsolutePath());
            email.attach(attachment);

            email.setDebug(LOGGER.isDebugEnabled());

            handler.send(email);
        } catch (final EmailException e) {
            throw new MonitorProTransportException("Failed to send email to MonitorPro", e);
        }
    }

    /**
     * Interface for the MonitorPro email transport handler
     */
    public interface EmailTransportHandler {

        /**
         * Send a file to MonitorPro (by email)
         *
         * @param email the {@link MultiPartEmail} to submit to MonitorPro
         * @return the message identifier generated by the underlying email system
         * @throws EmailException if a problem occurred when submitting the email to the SMTP server
         */
        String send(final MultiPartEmail email) throws EmailException;
    }

    /**
     * Default handler for submitting data to MonitorPro
     */
    private static class DefaultTransportHandler implements EmailTransportHandler {
        @Override
        public String send(final MultiPartEmail email) throws EmailException {
            return email.send();
        }
    }
}
