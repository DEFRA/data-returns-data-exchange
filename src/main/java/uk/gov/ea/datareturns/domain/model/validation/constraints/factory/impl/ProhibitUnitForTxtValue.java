package uk.gov.ea.datareturns.domain.model.validation.constraints.factory.impl;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.fields.FieldValue;
import uk.gov.ea.datareturns.domain.model.validation.constraints.factory.RecordConstraintValidator;

import javax.validation.ConstraintValidatorContext;

/**
 * Created by sam on 12/10/16.
 */
@Component
public class ProhibitUnitForTxtValue implements RecordConstraintValidator<DataSample> {
    @Override
    public boolean isValid(DataSample record, final ConstraintValidatorContext context) {
        boolean hasTxtValue = FieldValue.isNotEmpty(record.getTextValue());
        boolean hasUnit = FieldValue.isNotEmpty(record.getUnit());
        if (hasTxtValue && hasUnit) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{DR9050-Conflict}").addPropertyNode("unit").addConstraintViolation();
            return false;
        }
        return true;
    }
}
