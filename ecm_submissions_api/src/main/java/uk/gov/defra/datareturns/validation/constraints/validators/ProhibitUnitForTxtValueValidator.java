package uk.gov.defra.datareturns.validation.constraints.validators;

import org.springframework.stereotype.Component;
import uk.gov.defra.datareturns.data.model.record.Record;
import uk.gov.defra.datareturns.service.csv.EcmErrorCodes;
import uk.gov.defra.datareturns.validation.constraints.annotations.ProhibitUnitWithTxtValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Graham Willis.
 */
@Component
public class ProhibitUnitForTxtValueValidator implements ConstraintValidator<ProhibitUnitWithTxtValue, Record> {

    @Override
    public void initialize(final ProhibitUnitWithTxtValue prohibitUnitWithTxtValue) {

    }

    @Override
    public boolean isValid(final Record record, final ConstraintValidatorContext constraintValidatorContext) {
        final boolean hasTxtValue = record.getTextValue() != null;
        final boolean hasUnit = record.getUnit() != null;

        if (hasTxtValue && hasUnit) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(EcmErrorCodes.Conflict.PROHIBIT_UNIT_FOR_TXT_VALUE)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
