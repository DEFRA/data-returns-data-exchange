package uk.gov.ea.datareturns.tests.integration.resource;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Moved the RESTful tests which are outstanding from the API entity model here
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { App.class }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class LegacyResourceTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyResourceTests.class);
    public final static String URI = "http://localhost:%d/%s";
    public final static String CONTROLLED_LISTS = "api/v1/controlled-list/lists";
    public final static String TEST_SEARCH = "api/v1/lookup/permit?term=Dogsthorpe";
    public static final int SERVER_PORT = 9120;

    /*
     * Test basic controlled lists functionality
     */
    @Test
    public void testControlledListBadList() {
        Client client = createClient("test Controlled List Bad List");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/not-a-list";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void testListMetadata() {
        Client client = createClient("test List Metadata");
        final String uri = createURIForStep(CONTROLLED_LISTS);
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListParameters() {
        Client client = createClient("test Controlled List Parameters");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/Parameter";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListUnits() {
        Client client = createClient("test Controlled List Units");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/Unit";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListRefPeriod() {
        Client client = createClient("test Controlled List Reference Period");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/Ref_Period";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListMethodOrStandard() {
        Client client = createClient("test Controlled List Method Or Standard");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/Meth_Stand";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListReturnType() {
        Client client = createClient("test Controlled List Return Type");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/Rtn_Type";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    /*
     * Run all the test again to test the caching
     */
    @Test
    public void testControlledListParameters2() {
        Client client = createClient("test Controlled List Parameters2");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/Parameter";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListUnits2() {
        Client client = createClient("test Controlled List Units2");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/Unit";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListRefPeriod2() {
        Client client = createClient("test Controlled List Reference Period2");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/Ref_Period";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListMethodOrStandard2() {
        Client client = createClient("test Controlled List Method Or Standard2");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/Meth_Stand";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testControlledListReturnType2() {
        Client client = createClient("test Controlled List Return Type2");
        final String uri = createURIForStep(CONTROLLED_LISTS) + "/Rtn_Type";
        Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    @Test
    public void testSearch() {
        final Client client = createClient("test Search");
        final String uri = createURIForStep(TEST_SEARCH);
        final Response response = client.target(uri).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
    }

    /**
     * Create's a Jersey Client object ready for POST request used in Upload
     * step
     *
     * @param testName
     * @return
     */
    private static Client createClient(final String testName) {
        LOGGER.info("Creating client for test " + testName);
        final ClientConfig clientConfig = new ClientConfig();
        final Client client = new JerseyClientBuilder().withConfig(clientConfig).build().register(MultiPartFeature.class);
        client.property(ClientProperties.READ_TIMEOUT, (5 * 60 * 1000));
        return client;
    }

    /**
     * Create URI
     *
     * @param step
     * @return
     */
    private static String createURIForStep(final String step) {
        return String.format(URI, SERVER_PORT, step);
    }
}
