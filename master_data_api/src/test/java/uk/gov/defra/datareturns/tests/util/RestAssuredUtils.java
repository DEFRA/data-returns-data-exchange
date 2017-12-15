package uk.gov.defra.datareturns.tests.util;


import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Slf4j
@Component
public class RestAssuredUtils {
    private static String nomenclatureJson(final String nomenclature) {
        return "{ \"nomenclature\": \"" + nomenclature + "\" }";
    }

    public ValidatableResponse createEntity(ApiResource resource, final String body) {
        return given()
                .contentType(ContentType.JSON)
                .body(body)
//                .log().all()
                .when()
                .post(resource.url())
                .then()
//                .log().all()
                .statusCode(HttpStatus.CREATED.value());
    }


    public ValidatableResponse createSimpleEntity(ApiResource resource, final String nomenclature) {
        return createEntity(resource, nomenclatureJson(nomenclature));
    }

}
