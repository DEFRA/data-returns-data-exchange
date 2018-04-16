package uk.gov.defra.datareturns.validation.validators.transfers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.defra.datareturns.data.model.transfers.OverseasTransfer;
import uk.gov.defra.datareturns.data.model.transfers.Transfer;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/**
 * Validate a transfer within a submission
 *
 * @author Sam Gardner-Dell
 */
@RequiredArgsConstructor
@Slf4j
public class TransferValidator implements ConstraintValidator<ValidTransfer, Transfer> {
    @Override
    public void initialize(final ValidTransfer constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Transfer transfer, final ConstraintValidatorContext context) {
        boolean valid = checkOneOfRecoveryOrDisposalSet(transfer, context);
        valid = checkOverseasSentForDisposal(transfer, context) && valid;
        valid = checkTotalGreaterThanOverseas(transfer, context) && valid;
        valid = checkTotalNotBrtWithOverseas(transfer, context) && valid;
        valid = checkOverseasDestinationNotOverseas(transfer, context) && valid;

        return valid;
    }

    private boolean checkOneOfRecoveryOrDisposalSet(final Transfer transfer, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (transfer.getWfdDisposalId() != null && transfer.getWfdRecoveryId() != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("TRANSFER_WFD_DISPOSAL_AND_RECOVERY_BOTH_SET").addConstraintViolation();
            valid = false;
        } else if (transfer.getWfdDisposalId() == null && transfer.getWfdRecoveryId() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("TRANSFER_WFD_DISPOSAL_AND_RECOVERY_NONE_SET").addConstraintViolation();
            valid = false;
        }
        return valid;
    }

    private boolean checkOverseasSentForDisposal(final Transfer transfer, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (transfer.getWfdDisposalId() != null && transfer.getOverseas() != null && !transfer.getOverseas().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("OVERSEAS_NOT_ALLOWED_FOR_DISPOSAL").addConstraintViolation();
            valid = false;
        }
        return valid;
    }

    private boolean checkTotalGreaterThanOverseas(final Transfer transfer, final ConstraintValidatorContext context) {
        boolean valid = true;

        final BigDecimal totalTransferTonnage = transfer.getTonnage();
        if (totalTransferTonnage != null && transfer.getOverseas() != null) {
            final BigDecimal overseasSum = transfer.getOverseas().stream()
                    .map(OverseasTransfer::getTonnage)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (overseasSum.compareTo(totalTransferTonnage) > 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("OVERSEAS_TONNAGE_EXCEEDS_TOTAL").addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }

    private boolean checkTotalNotBrtWithOverseas(final Transfer transfer, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (transfer.isBelowReportingThreshold() && !transfer.getOverseas().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("OVERSEAS_NOT_ALLOWED_WITH_BRT_TRANSFER").addConstraintViolation();
            valid = false;
        }
        return valid;
    }

    private boolean checkOverseasDestinationNotOverseas(final Transfer transfer, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (transfer.getOverseas() != null) {
            for (final OverseasTransfer overseas : transfer.getOverseas()) {
                if ("GB".equals(overseas.getDestinationAddress().getCountry())) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("OVERSEAS_DESTINATION_NOT_OVERSEAS").addConstraintViolation();
                    valid = false;
                    break;
                }
            }
        }
        return valid;
    }
}
