package uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.validators;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.DataSampleValidationObject;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.annotations.RequireCommentsForTextValueComment;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Graham Willis
 */
public class RequireCommentsForTextValueCommentValidator implements ConstraintValidator<RequireCommentsForTextValueComment, DataSampleValidationObject> {
    @Override
    public void initialize(RequireCommentsForTextValueComment requireUnitWithValue) {

    }

    @Override
    public boolean isValid(DataSampleValidationObject dataSampleValidationObject, ConstraintValidatorContext constraintValidatorContext) {

        boolean hasValue = !StringUtils.isEmpty(dataSampleValidationObject.getValue().getValue());
        boolean hasUnit = (dataSampleValidationObject.getUnit().getEntity() != null);

        if (hasValue && !hasUnit) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("DR9050-Conflict").addConstraintViolation();
            return false;
        }
        return true;
    }
}
