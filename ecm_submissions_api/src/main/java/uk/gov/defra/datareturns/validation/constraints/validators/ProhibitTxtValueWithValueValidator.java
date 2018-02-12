package uk.gov.defra.datareturns.validation.constraints.validators;

import org.apache.commons.lang3.StringUtils;
import uk.gov.defra.datareturns.data.model.record.Record;
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
        final boolean hasTxtValue = !StringUtils.isEmpty(record.getTextValue());
        final boolean hasValue = record.getNumericValue() != null;

        if (hasValue && hasTxtValue) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("DR9999-Conflict").addConstraintViolation();
            return false;
        }
        return true;
    }
}
