package uk.gov.defra.datareturns.tests.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Slf4j
@RequiredArgsConstructor
@Component
public class RestAssuredUtils {
    private final ObjectMapper mapper;

    private String toJson(final Map<String, Object> fields) {
        try {
            return mapper.writeValueAsString(fields);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ValidatableResponse createEntity(final ApiResource resource, final String body) {
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


    public ValidatableResponse createSimpleEntity(final ApiResource resource, final Map<String, Object> fields) {
        return createEntity(resource, toJson(fields));
    }

    public ValidatableResponse createSimpleEntity(final ApiResource resource, final String nomenclature) {
        return createSimpleEntity(resource, Collections.singletonMap("nomenclature", nomenclature));
    }

}
