package uk.gov.ea.datareturns.domain.validation.landfillmeasurement.constraints.record;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementFieldMessageMap;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementMvo;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.record.RecordConstraintValidator;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.FieldValue;

import javax.validation.ConstraintValidatorContext;

/**
 * Created by sam on 12/10/16.
 */
@Component
public class ProhibitUnitForTxtValueNew implements RecordConstraintValidator<LandfillMeasurementMvo> {
    @Override
    public boolean isValid(LandfillMeasurementMvo record, final ConstraintValidatorContext context) {
        boolean hasTxtValue = FieldValue.isNotEmpty(record.getTextValue());
        boolean hasUnit = FieldValue.isNotEmpty(record.getUnit());
        if (hasTxtValue && hasUnit) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(LandfillMeasurementFieldMessageMap.Conflict.ProhibitUnitForTxtValue).addConstraintViolation();
            return false;
        }
        return true;
    }
}
