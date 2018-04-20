package uk.gov.defra.datareturns.validation.validators.submission;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.hateoas.Link;
import uk.gov.defra.datareturns.data.model.releases.Release;
import uk.gov.defra.datareturns.data.model.submissions.Submission;
import uk.gov.defra.datareturns.data.model.transfers.Transfer;
import uk.gov.defra.datareturns.validation.service.MasterDataEntity;
import uk.gov.defra.datareturns.validation.service.MasterDataLookupService;
import uk.gov.defra.datareturns.validation.service.dto.MdBaseEntity;
import uk.gov.defra.datareturns.validation.validators.PiValidationUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;
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
    private final MasterDataLookupService lookupService;

    @Override
    public void initialize(final ValidSubmission constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Submission submission, final ConstraintValidatorContext context) {
        boolean valid = checkReportingReference(submission, context);
        valid = checkSubmissionYear(submission, context) && valid;
        valid = checkNace(submission, context) && valid;
        valid = checkSubmissionReleasesUnique(submission, context) && valid;
        valid = checkSubmissionTransfersUnique(submission, context) && valid;
        return valid;
    }

    private boolean checkReportingReference(final Submission submission, final ConstraintValidatorContext context) {
        // Test reporting reference known
        final Link reportingRefLink = MasterDataEntity.UNIQUE_IDENTIFIER.getEntityLink().expand(submission.getReportingReference());
        if (submission.getReportingReference() == null || lookupService.get(MdBaseEntity.class, reportingRefLink).orElse(null) == null) {
            return handleError(context, "SUBMISSION_REPORTING_REFERENCE_INVALID", b -> b.addPropertyNode("reportingReference"));
        }

        // Test reporting reference configured for PI
        try {
            PiValidationUtil.getPiRegime(lookupService, submission.getReportingReference());
            return true;
        } catch (final ValidationException e) {
            return handleError(context, "SUBMISSION_REPORTING_REFERENCE_NOT_CONFIGURED_FOR_PI", b -> b.addPropertyNode("reportingReference"));
        }
    }

    private boolean checkNace(final Submission submission, final ConstraintValidatorContext context) {
        if (submission.getStatus() != null && !submission.getStatus().isOpen()) {
            final Link naceLookupLink = MasterDataEntity.NACE_CLASS.getEntityLink().expand(submission.getNaceId());
            if (submission.getNaceId() == null || lookupService.get(MdBaseEntity.class, naceLookupLink).orElse(null) == null) {
                return handleError(context, "SUBMISSION_NACE_ID_INVALID", b -> b.addPropertyNode("naceId"));
            }
        }
        return true;
    }

    private boolean checkSubmissionYear(final Submission submission, final ConstraintValidatorContext context) {
        final int currentSubmissionYear = Calendar.getInstance().get(Calendar.YEAR) - 1;
        final int oldestAllowed = currentSubmissionYear - 20;
        if (submission.getApplicableYear() == null
                || submission.getApplicableYear() > currentSubmissionYear
                || submission.getApplicableYear() < oldestAllowed) {
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
