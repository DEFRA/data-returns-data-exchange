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
public class RequireValueOrTxtValue implements RecordConstraintValidator<DataSample> {
    @Override
    public boolean isValid(DataSample record, final ConstraintValidatorContext context) {
        boolean hasValue = FieldValue.isNotEmpty(record.getValue());
        boolean hasTxtValue = FieldValue.isNotEmpty(record.getTextValue());

        String error = null;
        if (!hasValue && !hasTxtValue) {
            error = MessageCodes.Missing.RequireValueOrTxtValue;
        } else if (hasValue && hasTxtValue) {
            error = MessageCodes.Conflict.RequireValueOrTxtValue;
        }

        if (error != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
            return false;
        }
        return true;
    }
}
