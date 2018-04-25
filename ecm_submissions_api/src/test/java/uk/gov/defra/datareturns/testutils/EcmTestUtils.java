package uk.gov.defra.datareturns.testutils;

import io.restassured.response.ValidatableResponse;
import org.springframework.http.HttpStatus;

import java.util.function.Consumer;

import static io.restassured.RestAssured.given;


/**
 * Test utilities for submission
 *
 * @author Sam Gardner-Dell
 */
public final class EcmTestUtils {

    private EcmTestUtils() {
    }

    public static void runMultipartUploadTest(final String resourceName, final Consumer<ValidatableResponse> responseAssertions) {
        ValidatableResponse response = null;

        try {
            response =
                    given()
                            .multiPart("file", resourceName, EcmTestUtils.class.getResourceAsStream(resourceName))
                            .when()
                            .post("/uploads")
                            .then()
                            .log().all();

            responseAssertions.accept(response);
        } finally {
            // If the call above created a submission, then it is deleted again here.
            if (response != null) {
                final String locationHeader = response.extract().header("Location");
                if (locationHeader != null) {
                    given()
                            .delete(locationHeader)
                            .then()
                            .log().all()
                            .statusCode(HttpStatus.NO_CONTENT.value());
                }
            }
        }
    }
}
