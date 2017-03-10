package uk.gov.ea.datareturns.tests.integration.metrics;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.rules.FieldDefinition;
import uk.gov.ea.datareturns.testsupport.InfluxDBTestImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.ea.datareturns.aspects.MetricsConstants.*;

/**
 * Tests that validation metrics are extracted correctly.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles({"IntegrationTests"})
public class ValidationMetricsAspectTests extends AbstractMetricsTests {
    private static final Long INVALID_FILE_UNIQUE_ERROR_COUNT = 9L;
    private static final Long INVALID_FILE_TOTAL_ERROR_COUNT = 16L;

    @Test
    public void testValidationEventMetricsForValidFile() {
        uploadFile("valid.csv");
        List<InfluxDBTestImpl.PointData> data = stub.getPointData().get(Common.MEASUREMENT_VALIDATION_EVENT);
        // One event just occurred...
        Assertions.assertThat(data).size().isEqualTo(1);
        InfluxDBTestImpl.PointData point = data.get(0);
        Assertions.assertThat(point.getTags().get(ValidationEvent.TAG_VALIDATION_STATUS)).isEqualTo("valid");
        Assertions.assertThat(point.getFields().get(ValidationEvent.FIELD_RECORD_COUNT)).isEqualTo(24L);
        Assertions.assertThat(point.getFields().get(ValidationEvent.FIELD_EA_ID_COUNT)).isEqualTo(1L);
        Assertions.assertThat(point.getFields().get(ValidationEvent.FIELD_RETURN_TYPE_COUNT)).isEqualTo(1L);
        Assertions.assertThat(point.getFields().get(ValidationEvent.FIELD_TOTAL_ERROR_COUNT)).isEqualTo(0L);
        Assertions.assertThat(point.getFields().get(ValidationEvent.FIELD_UNIQUE_ERROR_COUNT)).isEqualTo(0L);
        Assertions.assertThat(point.getFields().get(ValidationEvent.FIELD_RUNTIME_MS)).isInstanceOf(Long.class);
        Assertions.assertThat((Long) point.getFields().get(ValidationEvent.FIELD_RUNTIME_MS)).isGreaterThan(0);
    }

    @Test
    public void testValidationEventMetricsForInvalidFile() {
        uploadFile("invalid.csv");
        List<InfluxDBTestImpl.PointData> data = stub.getPointData().get(Common.MEASUREMENT_VALIDATION_EVENT);
        // One event just occurred...
        Assertions.assertThat(data).size().isEqualTo(1);
        InfluxDBTestImpl.PointData point = data.get(0);
        Assertions.assertThat(point.getTags().get(ValidationEvent.TAG_VALIDATION_STATUS)).isEqualTo("invalid");
        Assertions.assertThat(point.getFields().get(ValidationEvent.FIELD_RECORD_COUNT)).isEqualTo(4L);
        Assertions.assertThat(point.getFields().get(ValidationEvent.FIELD_EA_ID_COUNT)).isEqualTo(1L);
        Assertions.assertThat(point.getFields().get(ValidationEvent.FIELD_RETURN_TYPE_COUNT)).isEqualTo(2L);
        Assertions.assertThat(point.getFields().get(ValidationEvent.FIELD_TOTAL_ERROR_COUNT)).isEqualTo(INVALID_FILE_TOTAL_ERROR_COUNT);
        Assertions.assertThat(point.getFields().get(ValidationEvent.FIELD_UNIQUE_ERROR_COUNT)).isEqualTo(INVALID_FILE_UNIQUE_ERROR_COUNT);
        Assertions.assertThat(point.getFields().get(ValidationEvent.FIELD_RUNTIME_MS)).isInstanceOf(Long.class);
        Assertions.assertThat((Long) point.getFields().get(ValidationEvent.FIELD_RUNTIME_MS)).isGreaterThan(0);
    }

    @Test
    public void testValidationErrorMetricsForValidFile() {
        uploadFile("valid.csv");
        List<InfluxDBTestImpl.PointData> data = stub.getPointData().get(Common.MEASUREMENT_VALIDATION_ERROR);
        // One event just occurred...
        Assertions.assertThat(data).isNullOrEmpty();
    }

    @Test
    public void testValidationErrorMetricsForInvalidFile() {
        uploadFile("invalid.csv");
        List<InfluxDBTestImpl.PointData> data = stub.getPointData().get(Common.MEASUREMENT_VALIDATION_ERROR);
        // One event just occurred...
        Assertions.assertThat(data).size().isEqualTo(INVALID_FILE_UNIQUE_ERROR_COUNT.intValue());
        long totalErrorCount = data.stream()
                .mapToLong(p -> (Long) p.getFields().get(ValidationError.FIELD_ERROR_COUNT))
                .sum();
        Assertions.assertThat(totalErrorCount).isEqualTo(INVALID_FILE_TOTAL_ERROR_COUNT);

        Set<String> eaIds = data.stream().map(p -> p.getTags().get(ValidationError.TAG_EA_ID)).collect(Collectors.toSet());
        Assertions.assertThat(eaIds).containsExactly("42355");

        Set<String> expectedErrors = new HashSet<>(Arrays.asList(new String[] { "DR9010-Missing", "DR9010-Incorrect", "DR9020-Incorrect",
                "DR9030-Missing", "DR9050-Incorrect", "DR9060-Missing", "DR9070-Incorrect", "DR9090-Incorrect", "DR9999-Missing" }));

        Set<String> errors = data.stream().map(p -> p.getTags().get(ValidationError.TAG_ERROR)).collect(Collectors.toSet());
        Assertions.assertThat(errors).containsAll(expectedErrors);

        data.stream().forEach(p -> {
            String field = p.getTags().get(ValidationError.TAG_ERROR_FIELD);
            String errorTemplate = "{" + p.getTags().get(ValidationError.TAG_ERROR) + "}";

            String expectedErrorFields = MessageCodes.getFieldDependencies(errorTemplate).stream()
                    .map(FieldDefinition::getName)
                    .collect(Collectors.joining(","));

            Assertions.assertThat(field).isEqualTo(expectedErrorFields);
        });

    }
}