package uk.gov.ea.datareturns.domain.validation.datasample.constraints.record;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleFieldMessageMap;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleMvo;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.record.RecordConstraintValidator;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.FieldValue;

import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Created by sam on 12/10/16.
 */
@Component
public class RequireCommentsForTxtValueSeeCommentNew implements RecordConstraintValidator<DataSampleMvo> {
    private static final Pattern SEE_COMMENT_PATTERN = Pattern.compile("^See comment[s]?", Pattern.CASE_INSENSITIVE);

    @Override public boolean isValid(DataSampleMvo record, final ConstraintValidatorContext context) {
        boolean seeCommentSet = FieldValue.isNotEmpty(record.getTextValue())
                && SEE_COMMENT_PATTERN.matcher(record.getTextValue().getInputValue()).matches();
        boolean hasComment = FieldValue.isNotEmpty(record.getComments());
        if (seeCommentSet && !hasComment) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(DataSampleFieldMessageMap.Missing.RequireCommentsForTxtValue).addConstraintViolation();
            return false;
        }
        return true;
    }
}
