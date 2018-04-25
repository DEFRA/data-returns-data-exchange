package uk.gov.defra.datareturns.validation.constraints.validators;

import org.apache.commons.lang3.StringUtils;
import uk.gov.defra.datareturns.data.model.record.Record;
import uk.gov.defra.datareturns.service.csv.EcmErrorCodes;
import uk.gov.defra.datareturns.validation.constraints.annotations.RequireCommentsForTextValueComment;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RequireCommentsForTextValueCommentValidator implements ConstraintValidator<RequireCommentsForTextValueComment, Record> {
    private static final Long SEE_COMMENT_ID = 6L;

    @Override
    public void initialize(final RequireCommentsForTextValueComment requireUnitWithValue) {

    }

    @Override
    public boolean isValid(final Record record, final ConstraintValidatorContext constraintValidatorContext) {
        final boolean seeCommentSet = SEE_COMMENT_ID.equals(record.getTextValue());
        final boolean hasComment = !StringUtils.isEmpty(record.getComments());

        if (seeCommentSet && !hasComment) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(EcmErrorCodes.Missing.REQUIRE_COMMENTS_FOR_TXT_VALUE)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
