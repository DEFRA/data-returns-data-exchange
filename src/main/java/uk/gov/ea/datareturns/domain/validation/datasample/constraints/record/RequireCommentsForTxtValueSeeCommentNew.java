package uk.gov.ea.datareturns.domain.validation.datasample.constraints.record;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleValidationObject;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.record.RecordConstraintValidator;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.FieldValue;

import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Created by sam on 12/10/16.
 */
@Component
public class RequireCommentsForTxtValueSeeCommentNew implements RecordConstraintValidator<DataSampleValidationObject> {
    private static final Pattern SEE_COMMENT_PATTERN = Pattern.compile("^See comment[s]?", Pattern.CASE_INSENSITIVE);

    @Override public boolean isValid(DataSampleValidationObject record, final ConstraintValidatorContext context) {
        boolean seeCommentSet = FieldValue.isNotEmpty(record.getTextValue())
                && SEE_COMMENT_PATTERN.matcher(record.getTextValue().getInputValue()).matches();
        boolean hasComment = FieldValue.isNotEmpty(record.getComments());
        if (seeCommentSet && !hasComment) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("DR9140-Missing").addConstraintViolation();
            return false;
        }
        return true;
    }
}
