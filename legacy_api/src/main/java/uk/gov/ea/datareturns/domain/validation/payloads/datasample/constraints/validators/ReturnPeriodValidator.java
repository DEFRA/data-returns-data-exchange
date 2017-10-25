package uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.validators;

import liquibase.util.StringUtils;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.annotations.ValidReturnPeriod;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.rules.ReturnPeriodFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * Validates Return Period values
 *
 * @author Sam Gardner-Dell
 */
public class ReturnPeriodValidator implements ConstraintValidator<ValidReturnPeriod, Object> {

    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(final ValidReturnPeriod constraintAnnotation) {
    }

    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        String returnPeriodValue = Objects.toString(value, null);
        return StringUtils.isEmpty(returnPeriodValue) || ReturnPeriodFormat.from(returnPeriodValue) != null;
    }
}