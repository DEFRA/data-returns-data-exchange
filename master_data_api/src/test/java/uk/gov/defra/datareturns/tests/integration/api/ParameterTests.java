package uk.gov.defra.datareturns.tests.integration.api;

import io.restassured.response.ValidatableResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.defra.datareturns.MasterDataApi;
import uk.gov.defra.datareturns.tests.rules.RestAssuredRule;
import uk.gov.defra.datareturns.tests.util.ApiResource;
import uk.gov.defra.datareturns.tests.util.RestAssuredUtils;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MasterDataApi.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
@Slf4j
public class ParameterTests {
    @Inject
    @Rule
    public RestAssuredRule restAssuredRule;

    @Inject
    private RestAssuredUtils rest;

    @Test
    public void testAddParameter() {
        final String nomen = RandomStringUtils.randomAlphabetic(8);
        final ValidatableResponse response = rest.createSimpleEntity(ApiResource.PARAMETERS, nomen);
        response.body("nomenclature", equalTo(nomen));
    }

    @Test
    public void testAddParameterAndGroup() {
        // Create a new parameter group
        final String groupName = RandomStringUtils.randomAlphabetic(8);
        final ValidatableResponse groupPostResponse = rest.createSimpleEntity(ApiResource.PARAMETER_GROUPS, groupName);
        groupPostResponse.body("nomenclature", equalTo(groupName));
        // Record the URI for the parameter collection for the new group
        final String parameterGroupParamsCollectionLink = groupPostResponse.extract().jsonPath().getString("_links.parameters.href");

        // Create a new parameter
        final String parameterName = RandomStringUtils.randomAlphabetic(8);
        final ValidatableResponse parameterPostResponse = rest.createSimpleEntity(ApiResource.PARAMETERS, parameterName);
        parameterPostResponse.body("nomenclature", equalTo(parameterName));
        // Record the URI for the new parameter
        final String parameterLocation = parameterPostResponse.extract().header("Location");


        // Associate the parameter with the group we created (POST one or more parameter URI's to the parameter group parameter collection URI
        given()
                .contentType("text/uri-list").body(parameterLocation)
                .when()
                .post(parameterGroupParamsCollectionLink)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Do a get request to the parameter-group parameter collection
        given()
                .get(parameterGroupParamsCollectionLink)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("_embedded.parameters.size()", is(1))
                .body("_embedded.parameters[0].nomenclature", is(parameterName));
    }
}
