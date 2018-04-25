package uk.gov.defra.datareturns.validation.constraints.validators;

import liquibase.util.StringUtils;
import uk.gov.defra.datareturns.validation.constraints.annotations.ValidReturnPeriod;
import uk.gov.defra.datareturns.service.csv.fields.ReturnPeriodFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * Validates Return Period values
 *
 * @author Sam Gardner-Dell
 */
public class ReturnPeriodValidator implements ConstraintValidator<ValidReturnPeriod, String> {

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
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        final String returnPeriodValue = Objects.toString(value, null);
        return StringUtils.isEmpty(returnPeriodValue) || ReturnPeriodFormat.from(returnPeriodValue) != null;
    }
}
