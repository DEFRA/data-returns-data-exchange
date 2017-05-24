package uk.gov.ea.datareturns.domain.validation.datasample.constraints.record;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleValidationObject;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.record.RecordConstraintValidator;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.FieldValue;

import javax.validation.ConstraintValidatorContext;

/**
 * Created by sam on 12/10/16.
 */
@Component
public class RequireValueOrTxtValueNew implements RecordConstraintValidator<DataSampleValidationObject> {
    @Override
    public boolean isValid(DataSampleValidationObject record, final ConstraintValidatorContext context) {
        boolean hasValue = FieldValue.isNotEmpty(record.getValue());
        boolean hasTxtValue = FieldValue.isNotEmpty(record.getTextValue());

        String error = null;
        if (!hasValue && !hasTxtValue) {
            error = "DR9999-Missing";
        } else if (hasValue && hasTxtValue) {
            error = "DR9999-Conflict";
        }

        if (error != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
            return false;
        }
        return true;
    }
}
