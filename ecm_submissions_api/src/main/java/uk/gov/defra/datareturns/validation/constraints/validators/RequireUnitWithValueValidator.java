package uk.gov.defra.datareturns.validation.constraints.validators;

import org.apache.commons.lang3.StringUtils;
import uk.gov.defra.datareturns.data.model.record.Record;
import uk.gov.defra.datareturns.validation.constraints.annotations.RequireUnitWithValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Graham Willis
 */
public class RequireUnitWithValueValidator implements ConstraintValidator<RequireUnitWithValue, Record> {
    @Override
    public void initialize(final RequireUnitWithValue requireUnitWithValue) {

    }

    @Override
    public boolean isValid(final Record record, final ConstraintValidatorContext constraintValidatorContext) {
        final boolean hasValue = record.getNumericValue() != null;
        final boolean hasUnit = !StringUtils.isEmpty(record.getUnit());

        if (hasValue && !hasUnit) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("DR9050-Missing").addConstraintViolation();
            return false;
        }
        return true;
    }
}
