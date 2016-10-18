package uk.gov.ea.datareturns.domain.model.validation.constraints.factory.impl;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.fields.FieldValue;
import uk.gov.ea.datareturns.domain.model.validation.constraints.factory.RecordConstraintValidator;

import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Created by sam on 12/10/16.
 */
@Component
public class RequireCommentsForTxtValueSeeComment implements RecordConstraintValidator<DataSample> {
    private static final Pattern SEE_COMMENT_PATTERN = Pattern.compile("^See comment[s]?", Pattern.CASE_INSENSITIVE);

    @Override public boolean isValid(DataSample record, final ConstraintValidatorContext context) {
        boolean seeCommentSet = FieldValue.isNotEmpty(record.getTextValue())
                && SEE_COMMENT_PATTERN.matcher(record.getTextValue().getInputValue()).matches();
        boolean hasComment = FieldValue.isNotEmpty(record.getComments());
        if (seeCommentSet && !hasComment) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{DR9140-Missing}").addPropertyNode("comments").addConstraintViolation();
            return false;
        }
        return true;
    }
}
