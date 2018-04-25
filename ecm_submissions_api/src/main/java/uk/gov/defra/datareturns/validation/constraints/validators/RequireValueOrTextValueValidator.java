package uk.gov.defra.datareturns.validation.constraints.validators;

import uk.gov.defra.datareturns.data.model.record.Record;
import uk.gov.defra.datareturns.service.csv.EcmErrorCodes;
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
        final boolean hasTxtValue = record.getTextValue() != null;
        final boolean hasValue = record.getNumericValue() != null;

        if (!hasValue && !hasTxtValue) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(EcmErrorCodes.Missing.REQUIRE_VALUE_OR_TXT_VALUE)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
