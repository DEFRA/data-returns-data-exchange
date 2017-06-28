package uk.gov.ea.datareturns.domain.validation.datasample.constraints.validators;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleValidationObject;
import uk.gov.ea.datareturns.domain.validation.datasample.constraints.annotations.ProhibitTxtValueWithValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by graham on 11/05/17.
 */
public class ProhibitTxtValueWithValueValidator implements ConstraintValidator<ProhibitTxtValueWithValue, DataSampleValidationObject> {

    @Override
    public void initialize(ProhibitTxtValueWithValue prohibitTxtValueWithValue) {

    }

    @Override
    public boolean isValid(DataSampleValidationObject dataSampleValidationObject, ConstraintValidatorContext constraintValidatorContext) {

        boolean hasTxtValue = (dataSampleValidationObject.getTextValue().getEntity() != null);
        boolean hasValue = !StringUtils.isEmpty(dataSampleValidationObject.getValue().getValue());

        String error = null;
        if (!hasValue && !hasTxtValue) {
            error = "DR9999-Missing";
        } else if (hasValue && hasTxtValue) {
            error = "DR9999-Conflict";
        }

        if (error != null) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(error).addConstraintViolation();
            return false;
        }

        return true;
    }
}
