package uk.gov.defra.datareturns.validation.validators.transfers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.SetUtils;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import uk.gov.defra.datareturns.data.model.transfers.OverseasTransfer;
import uk.gov.defra.datareturns.data.model.transfers.Transfer;
import uk.gov.defra.datareturns.validation.service.MasterDataEntity;
import uk.gov.defra.datareturns.validation.service.MasterDataLookupService;
import uk.gov.defra.datareturns.validation.service.dto.MdBaseEntity;
import uk.gov.defra.datareturns.validation.service.dto.MdEwcActivity;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.defra.datareturns.validation.util.ValidationUtil.handleError;

/**
 * Validate a transfer within a submission
 *
 * @author Sam Gardner-Dell
 */
@RequiredArgsConstructor
@Slf4j
public class TransferValidator implements ConstraintValidator<ValidTransfer, Transfer> {
    private final MasterDataLookupService lookupService;

    @Override
    public void initialize(final ValidTransfer constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Transfer transfer, final ConstraintValidatorContext context) {
        boolean valid = checkRecoveryOrDisposalValid(transfer, context);
        valid = checkPositiveTonnages(transfer, context) && valid;
        valid = checkValidEwcActivityId(transfer, context) && valid;
        valid = checkOverseasSentForDisposal(transfer, context) && valid;
        valid = checkHazardousTransferNotBrt(transfer, context) && valid;
        valid = checkOverseasWithNonHazardousActivity(transfer, context) && valid;
        valid = checkTotalGreaterThanOverseas(transfer, context) && valid;
        valid = checkOverseasDestinationNotOverseas(transfer, context) && valid;
        return valid;
    }

    private boolean checkPositiveTonnages(final Transfer transfer, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (transfer.getTonnage() != null && BigDecimal.ZERO.compareTo(transfer.getTonnage()) >= 0) {
            valid = handleError(context, "TRANSFER_TONNAGE_NOT_GREATER_THAN_ZERO", b -> b.addPropertyNode("tonnage"));
        }

        for (final OverseasTransfer os : SetUtils.emptyIfNull(transfer.getOverseas())) {
            if (os.getTonnage() != null && BigDecimal.ZERO.compareTo(os.getTonnage()) >= 0) {
                valid = handleError(context, "OVERSEAS_TONNAGE_NOT_GREATER_THAN_ZERO", b -> b.addPropertyNode("tonnage"));
            }
        }
        return valid;
    }

    private boolean checkRecoveryOrDisposalValid(final Transfer transfer, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (transfer.getWfdDisposalId() != null && transfer.getWfdRecoveryId() != null) {
            valid = handleError(context, "TRANSFER_WFD_DISPOSAL_AND_RECOVERY_BOTH_SET");
        } else if (transfer.getWfdDisposalId() == null && transfer.getWfdRecoveryId() == null) {
            valid = handleError(context, "TRANSFER_WFD_DISPOSAL_AND_RECOVERY_NONE_SET");
        } else {
            // We have only one field set, now check that the data is actually valid
            if (transfer.getWfdDisposalId() != null) {
                final Set<String> allowedIds = lookupService.list(MdBaseEntity.class, MasterDataEntity.WFD_DISPOSAL_CODES.getCollectionLink())
                        .stream()
                        .map(MasterDataLookupService::getResourceId)
                        .collect(Collectors.toSet());
                if (!allowedIds.contains(String.valueOf(transfer.getWfdDisposalId()))) {
                    valid = handleError(context, "TRANSFER_WFD_DISPOSAL_CODE_INVALID");
                }
            }

            if (transfer.getWfdRecoveryId() != null) {
                final Set<String> allowedIds = lookupService.list(MdBaseEntity.class, MasterDataEntity.WFD_RECOVERY_CODES.getCollectionLink())
                        .stream()
                        .map(MasterDataLookupService::getResourceId)
                        .collect(Collectors.toSet());
                if (!allowedIds.contains(String.valueOf(transfer.getWfdRecoveryId()))) {
                    valid = handleError(context, "TRANSFER_WFD_RECOVERY_CODE_INVALID");
                }
            }
        }
        return valid;
    }

    private boolean checkValidEwcActivityId(final Transfer transfer, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (transfer.getEwcActivityId() == null) {
            valid = handleError(context, "TRANSFER_EWC_ACTIVITY_REQUIRED");
        } else {
            final Set<String> activityIds = lookupService.list(MdEwcActivity.class, MasterDataEntity.EWC_ACTIVITIES.getCollectionLink()).stream()
                    .map(MasterDataLookupService::getResourceId)
                    .collect(Collectors.toSet());
            if (!activityIds.contains(String.valueOf(transfer.getEwcActivityId()))) {
                valid = handleError(context, "TRANSFER_EWC_ACTIVITY_INVALID");
            }
        }
        return valid;
    }

    private boolean checkHazardousTransferNotBrt(final Transfer transfer, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (transfer.isBelowReportingThreshold()) {
            try {
                final Link ewcActivityLink = MasterDataEntity.EWC_ACTIVITIES.getEntityLink().expand(transfer.getEwcActivityId());
                final MdEwcActivity activity = lookupService.get(MdEwcActivity.class, ewcActivityLink);
                if (activity.isHazardous()) {
                    valid = handleError(context, "TRANSFER_BRT_NOT_ALLOWED_WITH_HAZARDOUS_EWC");
                }
            } catch (final HttpStatusCodeException e) {
                // Validation of overseas transfers with hazardous ewc activity skipped if ewc activity could not be found.
                if (!HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                    throw e;
                }
            }
        }
        return valid;
    }

    private boolean checkOverseasSentForDisposal(final Transfer transfer, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (transfer.getWfdDisposalId() != null && !SetUtils.emptyIfNull(transfer.getOverseas()).isEmpty()) {
            valid = handleError(context, "OVERSEAS_NOT_ALLOWED_FOR_DISPOSAL");
        }
        return valid;
    }

    private boolean checkOverseasWithNonHazardousActivity(final Transfer transfer, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (!SetUtils.emptyIfNull(transfer.getOverseas()).isEmpty()) {
            try {
                final Link ewcActivityLink = MasterDataEntity.EWC_ACTIVITIES.getEntityLink().expand(transfer.getEwcActivityId());
                final MdEwcActivity activity = lookupService.get(MdEwcActivity.class, ewcActivityLink);
                if (!activity.isHazardous()) {
                    valid = handleError(context, "OVERSEAS_NOT_ALLOWED_FOR_NON_HAZARDOUS_EWC_ACTIVITY");
                }
            } catch (final HttpStatusCodeException e) {
                // Validation of BRT with hazardous ewc activity skipped if ewc activity could not be found.
                if (!HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                    throw e;
                }
            }
        }
        return valid;
    }

    private boolean checkTotalGreaterThanOverseas(final Transfer transfer, final ConstraintValidatorContext context) {
        boolean valid = true;

        final BigDecimal totalTransferTonnage = transfer.getTonnage();
        if (totalTransferTonnage != null && transfer.getOverseas() != null) {
            final BigDecimal overseasSum = transfer.getOverseas().stream().map(OverseasTransfer::getTonnage).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (overseasSum.compareTo(totalTransferTonnage) > 0) {
                valid = handleError(context, "OVERSEAS_TONNAGE_EXCEEDS_TOTAL");
            }
        }
        return valid;
    }

    private boolean checkOverseasDestinationNotOverseas(final Transfer transfer, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (transfer.getOverseas() != null) {
            for (final OverseasTransfer overseas : transfer.getOverseas()) {
                if ("GB".equals(overseas.getDestinationAddress().getCountry())) {
                    valid = handleError(context, "OVERSEAS_DESTINATION_NOT_OVERSEAS");
                    break;
                }
            }
        }
        return valid;
    }
}
