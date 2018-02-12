package uk.gov.defra.datareturns.validation.constraints.validators;

import org.apache.commons.lang3.StringUtils;
import uk.gov.defra.datareturns.data.model.record.Record;
import uk.gov.defra.datareturns.validation.constraints.annotations.RequireCommentsForTextValueComment;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class RequireCommentsForTextValueCommentValidator implements ConstraintValidator<RequireCommentsForTextValueComment, Record> {

    private static final Pattern SEE_COMMENT_PATTERN = Pattern.compile("^See comment[s]?", Pattern.CASE_INSENSITIVE);

    @Override
    public void initialize(final RequireCommentsForTextValueComment requireUnitWithValue) {

    }

    @Override
    public boolean isValid(final Record record, final ConstraintValidatorContext constraintValidatorContext) {

        final boolean seeCommentSet = !StringUtils.isEmpty(record.getTextValue())
                && SEE_COMMENT_PATTERN.matcher(record.getTextValue()).matches();

        final boolean hasComment = !StringUtils.isEmpty(record.getComments());

        if (seeCommentSet && !hasComment) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("DR9140-Missing").addConstraintViolation();
            return false;
        }
        return true;
    }
}
