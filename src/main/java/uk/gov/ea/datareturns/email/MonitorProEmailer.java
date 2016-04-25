package uk.gov.ea.datareturns.email;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.ea.datareturns.config.DataExchangeConfiguration;
import uk.gov.ea.datareturns.config.email.MonitorProEmailSettings;
import uk.gov.ea.datareturns.domain.io.csv.DataReturnsCSVProcessor;
import uk.gov.ea.datareturns.domain.io.csv.generic.CSVModel;
import uk.gov.ea.datareturns.domain.model.MonitoringDataRecord;
import uk.gov.ea.datareturns.domain.model.rules.EmmaDatabase;
import uk.gov.ea.datareturns.exception.system.DRSystemException;

/**
 * Handles emails to monitor pro
 *
 * 
 * TODO: Refactor/rethink this - code moved in from DataExchangeResource (as is)
 * 
 */
public class MonitorProEmailer {
	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorProEmailer.class);

	private final DataExchangeConfiguration config;
	
	public MonitorProEmailer(final DataExchangeConfiguration config) {
		this.config = config;
	}
	
	
	// TODO move to own class hierarchy - also needs tests
	public void sendNotifications(final File returnsCSVFile) {
		LOGGER.debug("Sending Email with attachment '" + returnsCSVFile.getAbsolutePath() + "'");

		final MonitorProEmailSettings settings = this.config.getMonitorProEmailSettings();

		// 3. Read the CSV data into a model
		final CSVModel<MonitoringDataRecord> csvInput = new DataReturnsCSVProcessor().read(returnsCSVFile);

		if (csvInput.getRecords().size() < 1) {
			// No data to send - this should never happen but we need to protected against an ArrayIndexOutOfBounds exception
			throw new DRSystemException("There was no data to send to Emma");
		}
		final String permitNumber = csvInput.getRecords().get(0).getPermitNumber();

		try {
			final MultiPartEmail email = new MultiPartEmail();
			final EmmaDatabase type = EmmaDatabase.forUniqueId(permitNumber);
			final String subject = settings.getDatabaseName(type);

			email.setHostName(settings.getHost());
			email.setSmtpPort(settings.getPort());

			email.setSubject(subject);
			email.addTo(settings.getTo());
			email.setFrom(settings.getFrom());

			final String messageBody = StringUtils.replace(settings.getBody(), "{{EA_ID}}", permitNumber);
			email.setMsg(messageBody);
			email.setStartTLSEnabled(settings.isUseTLS());

			final EmailAttachment attachment = new EmailAttachment();
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription("Data Returns File Upload");
			attachment.setName("Environment Agency.csv");
			attachment.setPath(returnsCSVFile.getAbsolutePath());
			email.attach(attachment);

			email.setDebug(LOGGER.isDebugEnabled());
			email.send();
		} catch (final EmailException e1) {
			throw new DRSystemException(e1, "Failed to send email to MonitorPro");
		} catch (final Exception e2) {
			throw new DRSystemException(e2, "Failed to send email to MonitorPro");
		}

		LOGGER.debug("Email sent");
	}
}
