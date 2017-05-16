package constraints.impl;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.validation.model.DataSample;
import uk.gov.ea.datareturns.domain.validation.model.MessageCodes;
import uk.gov.ea.datareturns.domain.validation.model.fields.FieldValue;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.record.RecordConstraintValidator;

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
            context.buildConstraintViolationWithTemplate(MessageCodes.Conflict.ProhibitUnitForTxtValue).addConstraintViolation();
            return false;
        }
        return true;
    }
}
