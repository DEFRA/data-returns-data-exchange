package uk.gov.defra.datareturns.test;

import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.defra.datareturns.PiApi;
import uk.gov.defra.datareturns.test.rules.RestAssuredRule;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PiApi.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
public class SubmissionTests {
    @Inject
    @Rule
    public RestAssuredRule restAssuredRule;


    @Test
    public void testSimpleSubmission() throws IOException {
        final String testJson = IOUtils.toString(SubmissionTests.class.getResourceAsStream("/data/submission.json"), StandardCharsets.UTF_8);

        // Associate the parameter with the group we created (POST one or more parameter URI's to the parameter group parameter collection URI
        given()
                .contentType(ContentType.JSON)
                .body(testJson)
                .when()
                .post("/submissions")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

}
