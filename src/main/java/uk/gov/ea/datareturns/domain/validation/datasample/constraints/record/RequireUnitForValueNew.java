package uk.gov.ea.datareturns.domain.validation.datasample.constraints.record;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleFieldMessageMap;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleMvo;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.record.RecordConstraintValidator;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.FieldValue;

import javax.validation.ConstraintValidatorContext;

/**
 * Created by sam on 12/10/16.
 */
@Component
public class RequireUnitForValueNew implements RecordConstraintValidator<DataSampleMvo> {
    @Override
    public boolean isValid(DataSampleMvo record, final ConstraintValidatorContext context) {
        boolean hasValue = FieldValue.isNotEmpty(record.getValue());
        boolean hasUnit = FieldValue.isNotEmpty(record.getUnit());
        if (hasValue && !hasUnit) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(DataSampleFieldMessageMap.Missing.Unit);
            return false;
        }
        return true;
    }
}
