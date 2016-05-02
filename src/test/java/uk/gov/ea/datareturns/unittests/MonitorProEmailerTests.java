/**
 * 
 */
package uk.gov.ea.datareturns.unittests;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import uk.gov.ea.datareturns.config.email.MonitorProEmailConfiguration;
import uk.gov.ea.datareturns.email.MonitorProEmailer;
import uk.gov.ea.datareturns.exception.application.DRHeaderMandatoryFieldMissingException;
import uk.gov.ea.datareturns.exception.system.DRSystemException;

/**
 * Unit tests for the monitorpro email functionality
 * 
 * @author Sam Gardner-Dell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({MonitorProEmailer.class})
public class MonitorProEmailerTests {
	private static File testSuccessFile;
	private static File testEmptyFile;
	private static File testHeaderOnlyFile;
	
	private static MultiPartEmail mockedClient;
	
	private static final String EMAIL_FROM = "data-returns-test-source@environment-agency.gov.uk";
	private static final String EMAIL_TO = "data-returns-test-target@environment-agency.gov.uk";
	private static final String EMAIL_BODY_TEMPLATE = "Data Returns Import file received for EA Identifier {{EA_ID}}";
	private static final String EMAIL_BODY_RESULT = "Data Returns Import file received for EA Identifier AB3002SQ";
	private static final String EMAIL_HOST = "unittest.localhost";
	private static final int EMAIL_PORT = 1234;
	
	private MonitorProEmailConfiguration emailSettings;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		testSuccessFile = getTestFile("/testfiles/email-unittest.csv");
		testEmptyFile = getTestFile("/testfiles/empty.csv");
		testHeaderOnlyFile = getTestFile("/testfiles/header-row-only.csv");
		mockedClient = PowerMockito.mock(MultiPartEmail.class);
		PowerMockito.whenNew(MultiPartEmail.class).withNoArguments().thenReturn(mockedClient);
		PowerMockito.doReturn("testReturnValue").when(mockedClient).send();
	}
	
	@Before
	public void beforeTest() throws Exception {
		mockedClient = PowerMockito.mock(MultiPartEmail.class);
		PowerMockito.whenNew(MultiPartEmail.class).withNoArguments().thenReturn(mockedClient);
		PowerMockito.doReturn("testReturnValue").when(mockedClient).send();
		
		emailSettings = new MonitorProEmailConfiguration();
		emailSettings.setFrom(EMAIL_FROM);
		emailSettings.setTo(EMAIL_TO);
		emailSettings.setBody(EMAIL_BODY_TEMPLATE);
		emailSettings.setHost(EMAIL_HOST);
		emailSettings.setPort(EMAIL_PORT);
		emailSettings.setUseTLS(false);
		
		emailSettings.setSubjectLowerNumericUniqueId("LOWER_NUMERIC");
		emailSettings.setSubjectUpperNumericUniqueId("UPPER_NUMERIC");
		emailSettings.setSubjectLowerAlphaNumericUniqueId("LOWER_ALPHANUMERIC");
		emailSettings.setSubjectUpperAlphaNumericUniqueId("UPPER_ALPHANUMERIC");
	}

	@Test
	public void testSuccessCase() throws Exception {
		MonitorProEmailer emailer = new MonitorProEmailer(emailSettings);
		emailer.sendNotifications(testSuccessFile);
		
		// Expectations
		Mockito.verify(mockedClient).setHostName(EMAIL_HOST);
		Mockito.verify(mockedClient).setSmtpPort(EMAIL_PORT);
		Mockito.verify(mockedClient).setStartTLSEnabled(false);
		
		Mockito.verify(mockedClient).setFrom(EMAIL_FROM);
		Mockito.verify(mockedClient).addTo(EMAIL_TO);
		Mockito.verify(mockedClient).setSubject("LOWER_ALPHANUMERIC");
		Mockito.verify(mockedClient).setMsg(EMAIL_BODY_RESULT);
		
		ArgumentCaptor<EmailAttachment> attachmentCaptor = ArgumentCaptor.forClass(EmailAttachment.class);
		Mockito.verify(mockedClient).attach(attachmentCaptor.capture());
		
		EmailAttachment attach = attachmentCaptor.getValue();
		Assert.assertEquals("Environment Agency.csv", attach.getName());
		Assert.assertEquals(testSuccessFile.getAbsolutePath(), attach.getPath());
	}
	
	@Test(expected=DRHeaderMandatoryFieldMissingException.class)
	public void testEmptyFile() throws Exception {
		MonitorProEmailer emailer = new MonitorProEmailer(emailSettings);
		emailer.sendNotifications(testEmptyFile);
	}
	
	@Test(expected=DRSystemException.class)
	public void testHeaderOnlyFile() throws Exception {
		MonitorProEmailer emailer = new MonitorProEmailer(emailSettings);
		emailer.sendNotifications(testHeaderOnlyFile);
	}
	
	@Test(expected=DRSystemException.class)
	public void testEmailException() throws Exception {
		PowerMockito.doThrow(new EmailException()).when(mockedClient).send();
		MonitorProEmailer emailer = new MonitorProEmailer(emailSettings);
		emailer.sendNotifications(testSuccessFile);
	}
	
	private static File getTestFile(String location) throws URISyntaxException {
		URL fileURL = LocalStorageProviderTests.class.getResource(location);
		return new File(fileURL.toURI());
	}
}
