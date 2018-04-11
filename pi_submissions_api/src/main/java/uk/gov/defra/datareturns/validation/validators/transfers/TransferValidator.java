package uk.gov.defra.datareturns.validation.validators.transfers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.defra.datareturns.data.model.transfers.OffsiteWasteTransfer;
import uk.gov.defra.datareturns.data.model.transfers.OverseasWasteTransfer;

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
public class TransferValidator implements ConstraintValidator<ValidTransfer, OffsiteWasteTransfer> {
    @Override
    public void initialize(final ValidTransfer constraintAnnotation) {
    }

    @Override
    public boolean isValid(final OffsiteWasteTransfer transfer, final ConstraintValidatorContext context) {
        boolean valid = checkTotalGreaterThanOverseas(transfer, context);
        valid = checkTotalNotBrtWithOverseas(transfer, context) && valid;
        return valid;
    }

    private boolean checkTotalGreaterThanOverseas(final OffsiteWasteTransfer transfer, final ConstraintValidatorContext context) {
        boolean valid = true;

        final BigDecimal totalTransferTonnage = transfer.getTonnage();
        if (totalTransferTonnage != null) {
            final BigDecimal overseasSum = transfer.getOverseasTransfers().stream()
                    .map(OverseasWasteTransfer::getTonnage)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (overseasSum.compareTo(totalTransferTonnage) > 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("OVERSEAS_TONNAGE_EXCEEDS_TOTAL").addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }

    private boolean checkTotalNotBrtWithOverseas(final OffsiteWasteTransfer transfer, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (transfer.isBelowReportingThreshold() && !transfer.getOverseasTransfers().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("OVERSEAS_NOT_ALLOWED_WITH_BRT_TRANSFER").addConstraintViolation();
            valid = false;
        }
        return valid;
    }
}
