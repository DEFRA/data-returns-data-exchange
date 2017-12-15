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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MasterDataApi.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
@Slf4j
public class ApplicabilityTests {
    @Inject
    @Rule
    public RestAssuredRule restAssuredRule;

    @Inject
    private RestAssuredUtils rest;

    @Test
    public void testCreateApplicability() {
        final EntityAndGroupResponse parameterResponse = createEntityAndGroup(ApiResource.PARAMETER_GROUPS, ApiResource.PARAMETERS);
        final EntityAndGroupResponse rtnTypeResponse = createEntityAndGroup(ApiResource.RETURN_TYPE_GROUPS, ApiResource.RETURN_TYPES);
        final EntityAndGroupResponse eaIdResponse = createEntityAndGroup(ApiResource.UNIQUE_IDENTIFIER_GROUPS, ApiResource.UNIQUE_IDENTIFIERS);
        final EntityAndGroupResponse unitResponse = createEntityAndGroup(ApiResource.UNIT_GROUPS, ApiResource.UNITS);

        // Create a applicability
        final String applicName = RandomStringUtils.randomAlphabetic(8);
        final ValidatableResponse applicResponse = rest.createSimpleEntity(ApiResource.APPLICABILITIES, applicName);
        final String applicLocation = applicResponse.extract().header("Location");

        log.info(applicLocation);
    }


    private EntityAndGroupResponse createEntityAndGroup(ApiResource groupResource, ApiResource entityResource) {
        // Create a new group
        final String pathExtractString = "_links." + entityResource.resourceName() + ".href";

        final String groupName = RandomStringUtils.randomAlphabetic(8);
        final String groupEntriesCollectionLink = rest.createSimpleEntity(groupResource, groupName)
                .extract().jsonPath().getString(pathExtractString);

        // Create a new entity
        final String entityName = RandomStringUtils.randomAlphabetic(8);
        final String entityLocation = rest.createSimpleEntity(entityResource, entityName).extract().header("Location");

        // Associate the parameter with the group
        given().contentType("text/uri-list").body(entityLocation)
                .when().post(groupEntriesCollectionLink)
                .then().statusCode(HttpStatus.NO_CONTENT.value());

        return new EntityAndGroupResponse(groupName, groupEntriesCollectionLink, entityName, entityLocation);
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    private static class EntityAndGroupResponse {
        private final String groupName;
        private final String groupEntriesCollectionLink;

        private final String entityName;
        private final String entityLocation;
    }

}
