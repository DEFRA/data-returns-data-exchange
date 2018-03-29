package uk.gov.defra.datareturns.tests.integration.api;

import io.restassured.response.ValidatableResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.defra.datareturns.testcommons.framework.WebIntegrationTest;
import uk.gov.defra.datareturns.tests.rules.RestAssuredRule;
import uk.gov.defra.datareturns.tests.util.ApiResource;
import uk.gov.defra.datareturns.tests.util.RestAssuredUtils;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@WebIntegrationTest
@Slf4j
public class ParameterIT {
    @Inject
    @Rule
    public RestAssuredRule restAssuredRule;

    @Inject
    private RestAssuredUtils rest;


    @Test
    public void testAddParameterAndGroup() {
        // Create a new parameter group
        final String parameterType = RandomStringUtils.randomAlphabetic(8);

        final ValidatableResponse createTypePostResponse = rest.createSimpleEntity(ApiResource.PARAMETER_TYPES, parameterType);
        createTypePostResponse.body("nomenclature", equalTo(parameterType));

        final Map<String, Object> groupData = new HashMap<>();
        groupData.put("nomenclature", RandomStringUtils.randomAlphabetic(8));

        final Map<String, Object> parameterData = new HashMap<>();
        parameterData.put("nomenclature", RandomStringUtils.randomAlphabetic(8));
        parameterData.put("type", createTypePostResponse.extract().header("Location"));

        final EntityAndGroupResponse response = createEntityWithinNewGroup(ApiResource.PARAMETER_GROUPS, groupData, ApiResource.PARAMETERS,
                parameterData);


        // Associate the parameter with the group we created (POST one or more parameter URI's to the parameter group parameter collection URI
        given()
                .contentType("text/uri-list").body(response.getEntityLocation())
                .when()
                .post(response.getGroupEntriesCollectionLink())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Do a get request to the parameter-group parameter collection
        given()
                .get(response.getGroupEntriesCollectionLink())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("_embedded.parameters.size()", is(1))
                .body("_embedded.parameters[0].nomenclature", is(parameterData.get("nomenclature")));
    }


    private EntityAndGroupResponse createEntityWithinNewGroup(final ApiResource groupResource, final Map<String, Object> groupResourceData,
                                                              final ApiResource entityResource, final Map<String, Object> entityResourceData) {
        // Create a new group
        final String pathExtractString = "_links." + entityResource.resourceName() + ".href";


        final ValidatableResponse createGroupPostResponse = rest.createSimpleEntity(groupResource, groupResourceData);
        final String groupEntriesCollectionLink = createGroupPostResponse.extract().jsonPath().getString(pathExtractString);

        // Create a new entity
        final ValidatableResponse createEntityPostResponse = rest.createSimpleEntity(entityResource, entityResourceData);
        final String entityLocation = createEntityPostResponse.extract().header("Location");

        // Associate the entity with the group
        given().contentType("text/uri-list").body(entityLocation)
                .when().post(groupEntriesCollectionLink)
                .then().statusCode(HttpStatus.NO_CONTENT.value());

        return new EntityAndGroupResponse(createGroupPostResponse, groupEntriesCollectionLink, createEntityPostResponse, entityLocation);
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    private static class EntityAndGroupResponse {
        private final ValidatableResponse groupResponse;
        private final String groupEntriesCollectionLink;
        private final ValidatableResponse entityResponse;
        private final String entityLocation;
    }
}
