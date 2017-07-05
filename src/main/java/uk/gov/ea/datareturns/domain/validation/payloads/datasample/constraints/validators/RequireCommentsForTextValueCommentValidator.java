package uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.validators;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.DataSampleValidationObject;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.annotations.RequireCommentsForTextValueComment;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class RequireCommentsForTextValueCommentValidator implements ConstraintValidator<RequireCommentsForTextValueComment, DataSampleValidationObject> {

    private static final Pattern SEE_COMMENT_PATTERN = Pattern.compile("^See comment[s]?", Pattern.CASE_INSENSITIVE);

    @Override
    public void initialize(RequireCommentsForTextValueComment requireUnitWithValue) {

    }

    @Override
    public boolean isValid(DataSampleValidationObject dataSampleValidationObject, ConstraintValidatorContext constraintValidatorContext) {

        boolean seeCommentSet = !StringUtils.isEmpty(dataSampleValidationObject.getTextValue().getInputValue())
                && SEE_COMMENT_PATTERN.matcher(dataSampleValidationObject.getTextValue().getInputValue()).matches();

        boolean hasComment = !StringUtils.isEmpty(dataSampleValidationObject.getComments().getValue());

        if (seeCommentSet && !hasComment) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("DR9140-Missing").addConstraintViolation();
            return false;
        }
        return true;
    }
}
