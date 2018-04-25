package uk.gov.defra.datareturns.validation.constraints.validators;

import org.apache.commons.lang3.StringUtils;
import uk.gov.defra.datareturns.service.csv.fields.MonitoringDate;
import uk.gov.defra.datareturns.validation.constraints.annotations.ValidMonitoringDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates Monitoring Date values
 *
 * @author Sam Gardner-Dell
 */
public class MonitoringDateValidator implements ConstraintValidator<ValidMonitoringDate, String> {
    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(final ValidMonitoringDate constraintAnnotation) {
    }

    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        boolean isValid = true;
        if (StringUtils.isNotEmpty(value)) {
            final MonitoringDate date = new MonitoringDate(value);
            isValid = date.isParsed();
        }
        return isValid;
    }
}
