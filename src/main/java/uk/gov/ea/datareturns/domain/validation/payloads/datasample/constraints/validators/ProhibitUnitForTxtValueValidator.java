package uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.validators;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.DataSampleValidationObject;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.annotations.ProhibitUnitWithTxtValue;

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
        boolean hasTxtValue = !StringUtils.isEmpty(dataSampleValidationObject.getTextValue().getInputValue());
        boolean hasUnit = !StringUtils.isEmpty(dataSampleValidationObject.getUnit().getInputValue());

        if (hasTxtValue && hasUnit) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("DR9050-Conflict").addConstraintViolation();
            return false;
        }
        return true;
    }
}
