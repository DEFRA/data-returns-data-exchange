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
@Component
public class MonitorProEmailer {
	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorProEmailer.class);

	private final MonitorProEmailConfiguration settings;

	@Inject
	public MonitorProEmailer(final MonitorProEmailConfiguration settings) {
		this.settings = settings;
	}

	public void sendNotifications(final File returnsCSVFile) {
		sendNotifications(returnsCSVFile, new DefaultTransportHandler());
	}
	
	public void sendNotifications(final File returnsCSVFile, EmailTransportHandler handler) {
		LOGGER.debug("Sending Email with attachment '" + returnsCSVFile.getAbsolutePath() + "'");

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
			final String subject = this.settings.getDatabaseName(type);

			email.setHostName(this.settings.getHost());
			email.setSmtpPort(this.settings.getPort());

			email.setSubject(subject);
			email.addTo(this.settings.getTo());
			email.setFrom(this.settings.getFrom());

			final String messageBody = StringUtils.replace(this.settings.getBody(), "{{EA_ID}}", permitNumber);
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
		} catch (final EmailException e1) {
			throw new DRSystemException(e1, "Failed to send email to MonitorPro");
		} catch (final Throwable e2) {
			throw new DRSystemException(e2, "Failed to send email to MonitorPro");
		}

		LOGGER.debug("Email sent");
	}
	
	public interface EmailTransportHandler {
		String send(final MultiPartEmail email) throws EmailException;
	}
	
	private static class DefaultTransportHandler implements EmailTransportHandler {
		@Override
		public String send(MultiPartEmail email) throws EmailException {
			return email.send();
		}
		
	}
}
