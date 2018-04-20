package uk.gov.defra.datareturns.validation.validators;

import org.springframework.hateoas.Link;
import uk.gov.defra.datareturns.data.Context;
import uk.gov.defra.datareturns.validation.service.MasterDataLookupService;
import uk.gov.defra.datareturns.validation.service.dto.MdRegime;

import javax.validation.ValidationException;
import java.util.List;

/**
 * Helper utility methods for pollution inventory validation functionality
 *
 * @author Sam Gardner-Dell
 */
public final class PiValidationUtil {

    /**
     * Private utlity class constructor
     */
    private PiValidationUtil() {
    }

    /**
     * Retrieve the pollution inventory regime associated with the given reporting reference.
     *
     * @param lookupService      the lookup service to use
     * @param reportingReference the reporting reference number to lookup
     * @return the pollution inventory regime associated with the given reporting reference, IF exactly one was found
     * @throws ValidationException if there is not exactly 1 pollution inventory regime associated with the given reporting reference
     */
    public static MdRegime getPiRegime(final MasterDataLookupService lookupService, final Object reportingReference) throws ValidationException {
        final Link regimesLookup = MdRegime.findRegimesForContextAndUniqueIdentifier(Context.PI, reportingReference);
        final List<MdRegime> regimes = lookupService.list(MdRegime.class, regimesLookup).orThrow();
        if (regimes.size() != 1) {
            throw new ValidationException("Pollution inventory submissions should be mapped to a single pollution inventory regime.");
        }
        return regimes.get(0);
    }
}
