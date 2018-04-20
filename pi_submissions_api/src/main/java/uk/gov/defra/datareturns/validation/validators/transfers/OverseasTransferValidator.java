package uk.gov.defra.datareturns.validation.validators.transfers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.defra.datareturns.data.model.transfers.OverseasTransfer;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

import static uk.gov.defra.datareturns.validation.util.ValidationUtil.handleError;

/**
 * Validate an overseas transfer
 *
 * @author Sam Gardner-Dell
 */
@RequiredArgsConstructor
@Slf4j
public class OverseasTransferValidator implements ConstraintValidator<ValidOverseasTransfer, OverseasTransfer> {
    @Override
    public void initialize(final ValidOverseasTransfer constraintAnnotation) {
    }

    @Override
    public boolean isValid(final OverseasTransfer transfer, final ConstraintValidatorContext context) {
        boolean valid = checkRequiredFields(transfer, context);
        valid = checkPositiveTonnage(transfer, context) && valid;
        valid = checkOverseasDestinationNotOverseas(transfer, context) && valid;
        return valid;
    }

    private boolean checkRequiredFields(final OverseasTransfer transfer, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (transfer.getResponsibleCompanyName() == null) {
            valid = handleError(context, "OVERSEAS_RESPONSIBLE_COMPANY_NAME_NOT_SPECIFIED", b -> b.addPropertyNode("responsibleCompanyName"));
        }
        if (transfer.getDestinationAddress() == null) {
            valid = handleError(context, "OVERSEAS_DESTINATION_ADDRESS_NOT_SPECIFIED", b -> b.addPropertyNode("destinationAddress"));
        }
        if (transfer.getResponsibleCompanyAddress() == null) {
            valid = handleError(context, "OVERSEAS_RESPONSIBLE_COMPANY_ADDRESS_NOT_SPECIFIED",
                    b -> b.addPropertyNode("responsibleCompanyAddress"));
        }
        if (transfer.getTonnage() == null) {
            valid = handleError(context, "OVERSEAS_TONNAGE_NOT_SPECIFIED", b -> b.addPropertyNode("tonnage"));
        }
        if (transfer.getMethod() == null) {
            valid = handleError(context, "OVERSEAS_METHOD_NOT_SPECIFIED", b -> b.addPropertyNode("method"));
        }
        return valid;
    }

    private boolean checkPositiveTonnage(final OverseasTransfer transfer, final ConstraintValidatorContext context) {
        boolean valid = true;
        if (transfer.getTonnage() != null && BigDecimal.ZERO.compareTo(transfer.getTonnage()) >= 0) {
            valid = handleError(context, "OVERSEAS_TONNAGE_NOT_GREATER_THAN_ZERO", b -> b.addPropertyNode("tonnage"));
        }
        return valid;
    }

    private boolean checkOverseasDestinationNotOverseas(final OverseasTransfer transfer, final ConstraintValidatorContext context) {
        if (transfer.getDestinationAddress() != null && "GB".equals(transfer.getDestinationAddress().getCountry())) {
            return handleError(context, "OVERSEAS_DESTINATION_NOT_OVERSEAS");
        }
        return true;
    }
}
