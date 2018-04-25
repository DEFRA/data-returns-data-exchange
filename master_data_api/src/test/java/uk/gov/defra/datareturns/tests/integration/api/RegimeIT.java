package uk.gov.defra.datareturns.tests.integration.api;

import io.restassured.response.ValidatableResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.defra.datareturns.testcommons.framework.WebIntegrationTest;
import uk.gov.defra.datareturns.testcommons.restassured.RestAssuredRule;
import uk.gov.defra.datareturns.tests.util.ApiResource;
import uk.gov.defra.datareturns.tests.util.RestAssuredUtils;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@RunWith(SpringRunner.class)
@WebIntegrationTest
@Slf4j
public class RegimeIT {
    @Inject
    @Rule
    public RestAssuredRule restAssuredRule;

    @Inject
    private RestAssuredUtils rest;

    @Test
    public void testCreateRegime() {
        // Create a regime
        final String regimeName = RandomStringUtils.randomAlphabetic(8);

        final Map<String, Object> regimeData = new HashMap<>();
        regimeData.put("nomenclature", regimeName);
        regimeData.put("context", "ECM");

        final ValidatableResponse regimeResponse = rest.createSimpleEntity(ApiResource.REGIMES, regimeData);
        final String regimeLocation = regimeResponse.extract().header("Location");

        // Now delete the regime
        given()
                .when()
                .delete(regimeLocation)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Check deleted
        given()
                .when()
                .get(regimeLocation)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
