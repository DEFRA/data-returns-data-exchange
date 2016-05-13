package uk.gov.ea.datareturns.email;

import java.io.File;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import uk.gov.ea.datareturns.config.email.MonitorProEmailConfiguration;
import uk.gov.ea.datareturns.domain.io.csv.DataReturnsCSVProcessor;
import uk.gov.ea.datareturns.domain.io.csv.generic.CSVModel;
import uk.gov.ea.datareturns.domain.model.EaId;
import uk.gov.ea.datareturns.domain.model.MonitoringDataRecord;
import uk.gov.ea.datareturns.exception.application.AbstractValidationException;

/**
 * Handles emails to monitor pro
 *
 *
 * TODO: Refactor/rethink this - code moved in from DataExchangeResource (as is)
 *
 */
@Component
public class MonitorProTransportHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorProTransportHandler.class);

	private final MonitorProEmailConfiguration settings;

	@Inject
	public MonitorProTransportHandler(final MonitorProEmailConfiguration settings) {
		this.settings = settings;
	}

	public void sendNotifications(final File returnsCSVFile) throws MonitorProTransportException {
		sendNotifications(returnsCSVFile, new DefaultTransportHandler());
	}

	public void sendNotifications(final File returnsCSVFile, final EmailTransportHandler handler) throws MonitorProTransportException {
		LOGGER.debug("Sending Email with attachment '" + returnsCSVFile.getAbsolutePath() + "'");

		// 3. Read the CSV data into a model
		CSVModel<MonitoringDataRecord> csvInput = null;

		try {
			csvInput = new DataReturnsCSVProcessor().read(returnsCSVFile);
		} catch (final AbstractValidationException e) {
			// This should never happen at this point (or something went very wrong and a previous point in the process)
			// If we encounter this here it represents a system error, not a validation error
			throw new MonitorProTransportException("Failed to read output CSV file when sending content to datastore.");
		}

		if (csvInput.getRecords().size() < 1) {
			// No data to send - this should never happen but we need to protect against an ArrayIndexOutOfBounds exception
			throw new MonitorProTransportException("Encountered empty output CSV file when sending content to datastore.");
		}

		final EaId eaId = csvInput.getRecords().get(0).getEaId();
		try {
			final MultiPartEmail email = new MultiPartEmail();
			final String subject = this.settings.getDatabaseName(eaId.getType());

			email.setHostName(this.settings.getHost());
			email.setSmtpPort(this.settings.getPort());

			email.setSubject(subject);
			email.addTo(this.settings.getTo());
			email.setFrom(this.settings.getFrom());

			final String messageBody = StringUtils.replace(this.settings.getBody(), "{{EA_ID}}", eaId.getIdentifier());
			email.setMsg(messageBody);
			email.setStartTLSEnabled(this.settings.isUseTLS());

			final EmailAttachment attachment = new EmailAttachment();
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription("Data Returns File Upload");
			attachment.setName("Environment Agency.csv");
			attachment.setPath(returnsCSVFile.getAbsolutePath());
			email.attach(attachment);

			email.setDebug(LOGGER.isDebugEnabled());

			handler.send(email);
		} catch (final EmailException e) {
			throw new MonitorProTransportException("Failed to send email to MonitorPro", e);
		}
	}

	public interface EmailTransportHandler {
		String send(final MultiPartEmail email) throws EmailException;
	}

	private static class DefaultTransportHandler implements EmailTransportHandler {
		@Override
		public String send(final MultiPartEmail email) throws EmailException {
			return email.send();
		}

	}
}
