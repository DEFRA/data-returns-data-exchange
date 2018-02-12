package uk.gov.defra.datareturns.validation.constraints.validators;

import org.apache.commons.lang3.StringUtils;
import uk.gov.defra.datareturns.data.model.record.Record;
import uk.gov.defra.datareturns.validation.constraints.annotations.RequireValueOrTxtValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by graham on 11/05/17.
 */
public class RequireValueOrTextValueValidator implements ConstraintValidator<RequireValueOrTxtValue, Record> {

    @Override
    public void initialize(final RequireValueOrTxtValue prohibitTxtValueWithValue) {
    }

    @Override
    public boolean isValid(final Record record, final ConstraintValidatorContext constraintValidatorContext) {
        final boolean hasTxtValue = !StringUtils.isEmpty(record.getTextValue());
        final boolean hasValue = record.getNumericValue() != null;

        if (!hasValue && !hasTxtValue) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("DR9999-Missing").addConstraintViolation();
            return false;
        }
        return true;
    }
}
