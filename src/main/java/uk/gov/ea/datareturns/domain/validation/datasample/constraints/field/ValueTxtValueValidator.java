package uk.gov.ea.datareturns.domain.validation.datasample.constraints.field;

import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleMvo;
import uk.gov.ea.datareturns.domain.validation.datasample.constraints.annotations.ValueTxtValue;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.FieldValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by graham on 11/05/17.
 */
public class ValueTxtValueValidator implements ConstraintValidator<ValueTxtValue, DataSampleMvo> {


    @Override
    public void initialize(ValueTxtValue valueTxtValue) {

    }

    @Override
    public boolean isValid(DataSampleMvo landfillMeasurementMvo, ConstraintValidatorContext constraintValidatorContext) {
        boolean hasValue = FieldValue.isNotEmpty(landfillMeasurementMvo.getValue());
        boolean hasTxtValue = FieldValue.isNotEmpty(landfillMeasurementMvo.getTextValue());

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