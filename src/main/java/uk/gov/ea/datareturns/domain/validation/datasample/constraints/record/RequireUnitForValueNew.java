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
public class RequireUnitForValueNew implements RecordConstraintValidator<DataSampleValidationObject> {
    @Override
    public boolean isValid(DataSampleValidationObject record, final ConstraintValidatorContext context) {
        boolean hasValue = FieldValue.isNotEmpty(record.getValue());
        boolean hasUnit = FieldValue.isNotEmpty(record.getUnit());
        if (hasValue && !hasUnit) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("DR9050-Conflict");
            return false;
        }
        return true;
    }
}
