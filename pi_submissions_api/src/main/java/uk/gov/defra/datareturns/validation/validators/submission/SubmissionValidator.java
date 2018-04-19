package uk.gov.defra.datareturns.validation.validators.submission;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import uk.gov.defra.datareturns.data.model.releases.Release;
import uk.gov.defra.datareturns.data.model.submissions.Submission;
import uk.gov.defra.datareturns.data.model.transfers.Transfer;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import static uk.gov.defra.datareturns.validation.util.ValidationUtil.handleError;

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
        boolean valid = checkReportingReference(submission, context);
        valid = checkSubmissionYear(submission, context) && valid;
        valid = checkSubmissionReleasesUnique(submission, context) && valid;
        valid = checkSubmissionTransfersUnique(submission, context) && valid;
        return valid;
    }

    private boolean checkReportingReference(final Submission submission, final ConstraintValidatorContext context) {
        // TODO: Add validations for unique identifier
//        final Object reference = submission.getReportingReference();
//        http://localhost:9020/api/regimes/search/findRegimesForContextAndUniqueIdentifier?context=PI&id=uniqueIdentifiers/7


        return true;
    }

    private boolean checkSubmissionYear(final Submission submission, final ConstraintValidatorContext context) {
        final int currentSubmissionYear = Calendar.getInstance().get(Calendar.YEAR) - 1;
        final int oldestAllowed = currentSubmissionYear - 20;

        if (submission.getApplicableYear() > currentSubmissionYear || submission.getApplicableYear() < oldestAllowed) {
            return handleError(context, "SUBMISSION_DATE_INVALID", b -> b.addPropertyNode("applicableYear"));
        }
        return true;
    }

    private boolean checkSubmissionReleasesUnique(final Submission submission, final ConstraintValidatorContext context) {
        final Set<String> keys = new HashSet<>();
        for (final Release release : SetUtils.emptyIfNull(submission.getSubmissionReleases())) {
            if (release.getSubstanceId() != null && release.getRouteId() != null) {
                final String key = StringUtils.joinWith("|", release.getSubstanceId(), release.getRouteId());
                if (!keys.add(key)) {
                    return handleError(context, "SUBMISSION_RELEASES_NOT_UNIQUE", b -> b.addPropertyNode("submissionReleases"));
                }
            }
        }
        return true;
    }

    private boolean checkSubmissionTransfersUnique(final Submission submission, final ConstraintValidatorContext context) {
        final Set<String> keys = new HashSet<>();
        for (final Transfer transfer : SetUtils.emptyIfNull(submission.getSubmissionTransfers())) {
            if (transfer.getEwcActivityId() != null && (transfer.getWfdDisposalId() != null || transfer.getWfdRecoveryId() != null)) {
                final String key = StringUtils.joinWith("|", transfer.getEwcActivityId(), transfer.getWfdDisposalId(), transfer.getWfdRecoveryId());
                if (!keys.add(key)) {
                    return handleError(context, "SUBMISSION_TRANSFERS_NOT_UNIQUE", b -> b.addPropertyNode("submissionTransfers"));
                }
            }
        }
        return true;
    }
}
