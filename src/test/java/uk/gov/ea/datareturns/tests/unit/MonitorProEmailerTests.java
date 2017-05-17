/**
 *
 */
package uk.gov.ea.datareturns.tests.unit;

import org.apache.commons.mail.EmailException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.gov.ea.datareturns.config.email.MonitorProEmailConfiguration;
import uk.gov.ea.datareturns.domain.monitorpro.MonitorProTransportException;
import uk.gov.ea.datareturns.domain.monitorpro.MonitorProTransportHandler;

import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

/**
 * Unit tests for the monitorpro email functionality
 *
 * @author Sam Gardner-Dell
 */
public class MonitorProEmailerTests {
    private static File testSuccessFile;

    private static File testEmptyFile;

    private static File testHeaderOnlyFile;

    private static final String EMAIL_FROM = "data-returns-test-source@environment-agency.gov.uk";

    private static final String EMAIL_TO = "data-returns-test-target@environment-agency.gov.uk";

    private static final String EMAIL_BODY_TEMPLATE = "Data Returns Import file received for EA Identifier {{EA_ID}}";

    private static final String EMAIL_BODY_RESULT = "Data Returns Import file received for EA Identifier EP3136GK";

    private static final String EMAIL_HOST = "unittest.localhost";

    private static final int EMAIL_PORT = 1234;

    private static final String TEST_EMAIL = "unit-test@datareturns";

    private static final String TEST_FILE = "unit-test.csv";

    private MonitorProEmailConfiguration emailSettings;

    @BeforeClass
    public static void beforeClass() throws Exception {
        testSuccessFile = getTestFile("/testfiles/email-unittest.csv");
        testEmptyFile = getTestFile("/testfiles/empty.csv");
        testHeaderOnlyFile = getTestFile("/testfiles/header-row-only.csv");
    }

    @Before
    public void beforeTest() throws Exception {
        this.emailSettings = new MonitorProEmailConfiguration();
        this.emailSettings.setFrom(EMAIL_FROM);
        this.emailSettings.setTo(EMAIL_TO);
        this.emailSettings.setBody(EMAIL_BODY_TEMPLATE);
        this.emailSettings.setHost(EMAIL_HOST);
        this.emailSettings.setPort(EMAIL_PORT);
        this.emailSettings.setUseTLS(false);

        this.emailSettings.setSubjectLowerNumericUniqueId("LOWER_NUMERIC");
        this.emailSettings.setSubjectUpperNumericUniqueId("UPPER_NUMERIC");
        this.emailSettings.setSubjectLowerAlphaNumericUniqueId("LOWER_ALPHANUMERIC");
        this.emailSettings.setSubjectUpperAlphaNumericUniqueId("UPPER_ALPHANUMERIC");
    }

    @Test
    public void testSuccessCase() throws Exception {
        final MonitorProTransportHandler emailer = new MonitorProTransportHandler(this.emailSettings);
        emailer.sendNotifications(TEST_EMAIL, TEST_FILE, "EP3136GK", testSuccessFile, (email) -> {
            email.buildMimeMessage();

            Assert.assertEquals(email.getHostName(), EMAIL_HOST);
            Assert.assertEquals(Integer.parseInt(email.getSmtpPort()), EMAIL_PORT);
            Assert.assertEquals(email.isStartTLSEnabled(), false);
            Assert.assertEquals(email.getFromAddress().getAddress(), EMAIL_FROM);
            Assert.assertEquals(email.getToAddresses().get(0).getAddress(), EMAIL_TO);
            Assert.assertEquals(email.getSubject(), "LOWER_ALPHANUMERIC");

            try {
                final Object msgBody = ((MimeMultipart) email.getMimeMessage().getContent()).getBodyPart(0).getDataHandler()
                        .getContent();
                Assert.assertTrue(EMAIL_BODY_RESULT.equals(Objects.toString(msgBody)));
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
            return "test";
        });
    }

    @Test(expected = MonitorProTransportException.class)
    public void testEmptyFile() throws Exception {
        final MonitorProTransportHandler emailer = new MonitorProTransportHandler(this.emailSettings);
        emailer.sendNotifications(TEST_EMAIL, TEST_FILE, "EP3136GK", testEmptyFile);
    }

    @Test(expected = MonitorProTransportException.class)
    public void testHeaderOnlyFile() throws Exception {
        final MonitorProTransportHandler emailer = new MonitorProTransportHandler(this.emailSettings);
        emailer.sendNotifications(TEST_EMAIL, TEST_FILE, "EP3136GK", testHeaderOnlyFile);
    }

    @Test(expected = MonitorProTransportException.class)
    public void testEmailException() throws Exception {
        final MonitorProTransportHandler emailer = new MonitorProTransportHandler(this.emailSettings);
        emailer.sendNotifications(TEST_EMAIL, TEST_FILE, "EP3136GK", testSuccessFile, (email) -> {
            throw new EmailException("He's dead Jim");
        });
    }

    private static File getTestFile(final String location) throws URISyntaxException {
        final URL fileURL = MonitorProEmailerTests.class.getResource(location);
        return new File(fileURL.toURI());
    }
}
