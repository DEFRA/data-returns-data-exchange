package uk.gov.defra.datareturns.validation.validators.submission;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.defra.datareturns.data.model.submissions.Submission;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Calendar;

/**
 * Validate a submission object
 *
 * @author Sam Gardner-Dell
 */
@RequiredArgsConstructor
@Slf4j
public class SubmissionValidator implements ConstraintValidator<ValidSubmission, Submission> {

    @Override
    public void initialize(final ValidSubmission constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Submission submission, final ConstraintValidatorContext context) {
        boolean valid = validateReportingReference(context, submission);
        valid = valid && validateSubmissionYear(context, submission);
        return valid;
    }

    private boolean validateReportingReference(final ConstraintValidatorContext context, final Submission submission) {
        // TODO: Add validations for unique identifier
//        final Object reference = submission.getReportingReference();
//        http://localhost:9020/api/regimes/search/findRegimesForContextAndUniqueIdentifier?context=PI&id=uniqueIdentifiers/7
        return true;
    }

    private boolean validateSubmissionYear(final ConstraintValidatorContext context, final Submission submission) {
        final int currentSubmissionYear = Calendar.getInstance().get(Calendar.YEAR) - 1;
        final int oldestAllowed = currentSubmissionYear - 20;

        if (submission.getApplicableYear() > currentSubmissionYear || submission.getApplicableYear() < oldestAllowed) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("INVALID_SUBMISSION_DATE").addPropertyNode("applicableYear").addConstraintViolation();
            return false;
        }
        return true;
    }
}
