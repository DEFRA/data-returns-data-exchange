package uk.gov.ea.datareturns.domain.validation.landfillmeasurement.constraints.field;

import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementFieldMessageMap;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementMvo;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.constraints.annotations.ValueTxtValue;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.FieldValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by graham on 11/05/17.
 */
public class ValueTxtValueValidator implements ConstraintValidator<ValueTxtValue, LandfillMeasurementMvo> {


    @Override
    public void initialize(ValueTxtValue valueTxtValue) {

    }

    @Override
    public boolean isValid(LandfillMeasurementMvo landfillMeasurementMvo, ConstraintValidatorContext constraintValidatorContext) {
        boolean hasValue = FieldValue.isNotEmpty(landfillMeasurementMvo.getValue());
        boolean hasTxtValue = FieldValue.isNotEmpty(landfillMeasurementMvo.getTextValue());

        String error = null;
        if (!hasValue && !hasTxtValue) {
            error = LandfillMeasurementFieldMessageMap.Missing.RequireValueOrTxtValue;
        } else if (hasValue && hasTxtValue) {
            error = LandfillMeasurementFieldMessageMap.Conflict.RequireValueOrTxtValue;
        }

        if (error != null) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(error).addConstraintViolation();
            return false;
        }

        return true;
    }
}