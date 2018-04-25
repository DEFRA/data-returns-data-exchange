package uk.gov.defra.datareturns.validation.constraints.validators;

import uk.gov.defra.datareturns.data.model.record.Record;
import uk.gov.defra.datareturns.service.csv.EcmErrorCodes;
import uk.gov.defra.datareturns.validation.constraints.annotations.ProhibitTxtValueWithValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by graham on 11/05/17.
 */
public class ProhibitTxtValueWithValueValidator implements ConstraintValidator<ProhibitTxtValueWithValue, Record> {

    @Override
    public void initialize(final ProhibitTxtValueWithValue prohibitTxtValueWithValue) {
    }

    @Override
    public boolean isValid(final Record record, final ConstraintValidatorContext constraintValidatorContext) {
        final boolean hasTxtValue = record.getTextValue() != null;
        final boolean hasValue = record.getNumericValue() != null;

        if (hasValue && hasTxtValue) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate(EcmErrorCodes.Conflict.PROHIBIT_TEXT_VALUE_WITH_NUMERIC_VALUE)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
