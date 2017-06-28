package uk.gov.ea.datareturns.domain.validation.datasample.constraints.validators;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleValidationObject;
import uk.gov.ea.datareturns.domain.validation.datasample.constraints.annotations.ProhibitUnitWithTxtValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Graham Willis.
 */
@Component
public class ProhibitUnitForTxtValueValidator implements ConstraintValidator<ProhibitUnitWithTxtValue, DataSampleValidationObject> {

    @Override
    public void initialize(ProhibitUnitWithTxtValue prohibitUnitWithTxtValue) {

    }

    @Override
    public boolean isValid(DataSampleValidationObject dataSampleValidationObject, ConstraintValidatorContext constraintValidatorContext) {
        boolean hasTxtValue = (dataSampleValidationObject.getTextValue().getEntity() != null);
        boolean hasUnit = (dataSampleValidationObject.getUnit().getEntity() != null);

        if (hasTxtValue && hasUnit) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("DR9050-Conflict").addConstraintViolation();
            return false;
        }
        return true;
    }
}
