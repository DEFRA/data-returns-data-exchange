package uk.gov.defra.datareturns.validation.constraints.validators;

import org.apache.commons.lang3.StringUtils;
import uk.gov.defra.datareturns.validation.constraints.annotations.ValidMonitoringDate;
import uk.gov.defra.datareturns.validation.payloads.datasample.fields.MonitoringDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Validates Monitoring Date values
 *
 * @author Sam Gardner-Dell
 */
public class MonitoringDateValidator implements ConstraintValidator<ValidMonitoringDate, Object> {
    private ValidMonitoringDate constraintAnnotation;

    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(final ValidMonitoringDate annotation) {
        this.constraintAnnotation = annotation;
    }

    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        boolean isValid = false;
        if (value instanceof MonitoringDate) {
            final MonitoringDate monitoringDate = (MonitoringDate) value;
            isValid = monitoringDate.isParsed();

            if (isValid) {
                // The DEP makes no provision for timezone and the agreed assumption is that data is reported using the local time at
                // the time of measurement.
                final Instant instant = monitoringDate.getInstant();
                final Instant now = LocalDateTime.now().toInstant(ZoneOffset.ofHours(0));
                isValid = instant.equals(now) || instant.isBefore(now);

                // TODO: Future release - extend validation to check for dates too far in the past (should be configurable)
                // final Instant earliestAllowed = now.minus(18, ChronoUnit.MONTHS);
                // isValid = (instant.equals(now) || instant.isBefore(now)) && instant.isAfter(earliestAllowed);
            } else if (StringUtils.isEmpty(monitoringDate.getInputValue())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(constraintAnnotation.missingMessage()).addConstraintViolation();
            }
        }
        return isValid;
    }
}
