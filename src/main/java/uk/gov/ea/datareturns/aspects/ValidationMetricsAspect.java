package uk.gov.ea.datareturns.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.config.metrics.InfluxDBFacade;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.rules.FieldDefinition;
import uk.gov.ea.datareturns.domain.result.ValidationError;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;
import uk.gov.ea.datareturns.util.Environment;
import uk.gov.ea.datareturns.util.StopWatch;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Aspect to gather metrics from the validation engine.
 *
 * @author Sam Gardner-Dell
 */
@Aspect
@Configurable
@Component
public class ValidationMetricsAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationMetricsAspect.class);

    @Inject
    private InfluxDBFacade influxdb;

    /**
     * Report validation metrics validation errors.
     *
     * @param pjp the pjp
     * @param model the model
     * @return the validation errors
     * @throws Throwable the throwable
     */
    @Around("Pointcuts.modelValidation(model)")
    public ValidationErrors reportValidationMetrics(ProceedingJoinPoint pjp, List<DataSample> model) throws Throwable {
        StopWatch sw = new StopWatch("validation_timer");
        ValidationErrors errors = null;
        try {
            sw.start("Validating model");
            errors = (ValidationErrors) pjp.proceed();
            return errors;
        } finally {
            sw.stop();
            if (errors == null) {
                LOGGER.warn("Unable to report validation metrics - null response from model validation.");
            } else {
                long timestamp = System.currentTimeMillis();
                long validationRuntime = sw.getLastTaskTimeMillis();
                writeValidationEventMetrics(timestamp, validationRuntime, model, errors);
                writeValidationErrorMetricsByEaId(timestamp, model, errors);
            }
        }
    }

    /**
     * Write validation metrics for each {@link uk.gov.ea.datareturns.domain.model.fields.impl.EaId} used.
     *
     * @param timestamp the timestamp of the validation event
     * @param model the model being validated
     * @param errors the errors returned by the validation process
     */
    private void writeValidationErrorMetricsByEaId(long timestamp, List<DataSample> model, ValidationErrors errors) {
        Map<String, List<ValidationError>> errorsByEaId = errors.getErrors().stream().collect(Collectors.groupingBy(e -> {
            DataSample row = model.get(e.getRecordIndex());
            return Optional.ofNullable(row.getEaId().getEntity()).map(UniqueIdentifier::getName).orElse("Unknown");
        }));

        errorsByEaId.entrySet().stream().forEach(eaIdEntry -> {
            String eaId = eaIdEntry.getKey();
            List<ValidationError> errorsForEaId = eaIdEntry.getValue();

            Map<String, List<ValidationError>> errorsByType = errorsForEaId.stream()
                    .collect(Collectors.groupingBy(ValidationMetricsAspect::getSpecificError));

            errorsByType.entrySet().stream().map(errorByTypeEntry -> {
                String error = errorByTypeEntry.getKey();
                List<ValidationError> errorsForType = errorByTypeEntry.getValue();
                String field = MessageCodes.getFieldDependencies("{" + error + "}").stream()
                        .map(FieldDefinition::getName)
                        .collect(Collectors.joining(","));

                String errorType = errorsForType.stream().findFirst().map(ValidationError::getErrorType).orElse("Unknown");
                Point.Builder point = Point.measurement(MetricsConstants.Common.MEASUREMENT_VALIDATION_ERROR)
                        .time(timestamp, TimeUnit.MILLISECONDS)
                        .tag(MetricsConstants.ValidationError.TAG_HOST, Environment.getHostname())
                        .tag(MetricsConstants.ValidationError.TAG_EA_ID, eaId)
                        .tag(MetricsConstants.ValidationError.TAG_ERROR, error)
                        .tag(MetricsConstants.ValidationError.TAG_ERROR_FIELD, field)
                        .tag(MetricsConstants.ValidationError.TAG_ERROR_TYPE, errorType)
                        .addField(MetricsConstants.ValidationError.FIELD_ERROR_COUNT, errorsForType.size());
                return point.build();
            }).forEach(influxdb::write);
        });
    }

    /**
     * Write high-level validation engine metrics
     *
     * @param timestamp the timestamp of the validation event
     * @param model the model being validated
     * @param errors the errors returned by the validation process
     */
    private void writeValidationEventMetrics(long timestamp, long validationRuntime, List<DataSample> model, ValidationErrors errors) {
        String validationStatus = errors.getErrors().isEmpty() ? "valid" : "invalid";
        Set<String> uniqueErrors = errors.getErrors().stream().map(ValidationMetricsAspect::getSpecificError).collect(Collectors.toSet());
        long countEaIds = model.stream().map(DataSample::getEaId).distinct().count();
        long countReturnTypes = model.stream().map(ds -> ds.getReturnType().getEntity()).distinct().count();

        influxdb.write(Point.measurement(MetricsConstants.Common.MEASUREMENT_VALIDATION_EVENT)
                .time(timestamp, TimeUnit.MILLISECONDS)
                .tag(MetricsConstants.ValidationEvent.TAG_VALIDATION_STATUS, validationStatus)
                .tag(MetricsConstants.ValidationEvent.TAG_HOST, Environment.getHostname())
                .addField(MetricsConstants.ValidationEvent.FIELD_RECORD_COUNT, model.size())
                .addField(MetricsConstants.ValidationEvent.FIELD_EA_ID_COUNT, countEaIds)
                .addField(MetricsConstants.ValidationEvent.FIELD_RETURN_TYPE_COUNT, countReturnTypes)
                .addField(MetricsConstants.ValidationEvent.FIELD_TOTAL_ERROR_COUNT, errors.getErrors().size())
                .addField(MetricsConstants.ValidationEvent.FIELD_UNIQUE_ERROR_COUNT, uniqueErrors.size())
                .addField(MetricsConstants.ValidationEvent.FIELD_RUNTIME_MS, validationRuntime)
                .build());
    }

    /**
     * Get a full error string from a {@link ValidationError} using {@link ValidationError#getErrorCode()} and {@link ValidationError#getErrorType()}
     * @param error the target error
     * @return the error string - e.g. DR9000-Invalid
     */
    private static final String getSpecificError(ValidationError error) {
        return String.format("DR%04d-%s", error.getErrorCode(), error.getErrorType());
    }
}