package uk.gov.defra.datareturns.validation.constraints.validators;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.defra.datareturns.data.model.record.Record;
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
        final boolean hasTxtValue = !StringUtils.isEmpty(record.getTextValue());
        final boolean hasUnit = !StringUtils.isEmpty(record.getUnit());

        if (hasTxtValue && hasUnit) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("DR9050-Conflict").addConstraintViolation();
            return false;
        }
        return true;
    }
}
