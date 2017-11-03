package uk.gov.defra.datareturns.tests.integration.api;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.defra.datareturns.MasterDataApi;
import uk.gov.defra.datareturns.data.loader.DatabaseLoader;

import javax.inject.Inject;
import java.util.Map;

import static io.restassured.RestAssured.basic;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.greaterThan;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MasterDataApi.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
@Slf4j
public class ApiTest {

    @Inject
    Map<String, DatabaseLoader> loaderBeans;

    /**
     * Set up RestAssured (via injection)
     *
     * @param port (injected) the server port
     */
    @LocalServerPort
    public void setup(int port) {
        RestAssured.reset();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = "/api";
        RestAssured.authentication = basic("user", "password");
        loaderBeans.forEach((k, l) -> l.load());
    }

    @Test
    public void testRestAssuredRequest() {
        when().get("/parameters")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("_embedded.parameters.size()", greaterThan(3000));
    }


//    @Test
//    public void testApiClientRequest() {
//        RestTemplate restTemplate = getRestTemplateWithHalMessageConverter();
//        ResponseEntity<Resource<Parameter>> responseEntity =
//                restTemplate.exchange("http://localhost:9120/api/parameters/1", HttpMethod.GET, null,
//                        new ParameterizedTypeReference<Resource<Parameter>>() {
//                        },
//                        Collections.emptyMap());
//        Parameter p = responseEntity.getBody().getContent();
//        log.info("ID:   " + p.getId());
//        log.info("CODE: " + p.getCode());
//        log.info("CAS:  " + p.getCas());
//        log.info("ETAG: " + responseEntity.getHeaders().getETag());

//        String newCasValue = "NewCasValue " + Math.random();
//        p.setCas(newCasValue);
//
//        ResponseEntity<Resource<Parameter>> updateResponse =
//                restTemplate.exchange("http://localhost:9120/api/parameters/1", HttpMethod.POST, new HttpEntity<>(p),
//                        new ParameterizedTypeReference<Resource<Parameter>>() {
//                        },
//                        Collections.emptyMap());
//
//        p = updateResponse.getBody().getContent();
//        log.info("ID:   " + p.getId());
//        log.info("NAME: " + p.getName());
//        log.info("CAS:  " + p.getCas());
//        log.info("ETAG: " + updateResponse.getHeaders().getETag());
//
//        Assert.assertEquals(p.getCas(), newCasValue);
//    }

//    @Test
//    public void testCollectionResource() {
//        RestTemplate restTemplate = getRestTemplateWithHalMessageConverter();
//
//        ResponseEntity<PagedResources<Resource<Parameter>>> responseEntity =
//                restTemplate.exchange("http://localhost:9120/api/parameters",
//                        HttpMethod.GET, null,
//                        new ParameterizedTypeReference<PagedResources<Resource<Parameter>>>() {
//                        },
//                        Collections.emptyMap());
//
//        if (responseEntity.getStatusCode() == HttpStatus.OK) {
//            PagedResources<Resource<Parameter>> parameterResource = responseEntity.getBody();
//            Collection<Resource<Parameter>> resources = parameterResource.getContent();
//            log.info("Found {} resources", resources.size());
//            Assert.assertTrue("One or more resources expected", !resources.isEmpty());
//
//        }
//    }
}
