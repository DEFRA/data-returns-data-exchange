package uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.validators;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.DataSampleValidationObject;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.annotations.RequireUnitWithValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Graham Willis
 */
public class RequireUnitWithValueValidator implements ConstraintValidator<RequireUnitWithValue, DataSampleValidationObject> {
    @Override
    public void initialize(RequireUnitWithValue requireUnitWithValue) {

    }

    @Override
    public boolean isValid(DataSampleValidationObject dataSampleValidationObject, ConstraintValidatorContext constraintValidatorContext) {

        boolean hasValue = !StringUtils.isEmpty(dataSampleValidationObject.getValue().getValue());
        boolean hasUnit = !StringUtils.isEmpty(dataSampleValidationObject.getUnit().getInputValue());

        if (hasValue && !hasUnit) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("DR9050-Missing").addConstraintViolation();
            return false;
        }
        return true;
    }
}
