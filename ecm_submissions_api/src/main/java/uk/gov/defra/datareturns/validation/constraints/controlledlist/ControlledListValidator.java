package uk.gov.defra.datareturns.validation.constraints.controlledlist;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.defra.datareturns.service.MasterDataLookupService;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * Allows validation against a controlled list of values
 *
 * @author Sam Gardner-Dell
 */
public class ControlledListValidator implements ConstraintValidator<ControlledList, Object> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final MasterDataLookupService lookupService;

    private String[] entityNames;

    @Inject
    public ControlledListValidator(final MasterDataLookupService lookupService) {
        this.lookupService = lookupService;
    }

    @Override
    public void initialize(final ControlledList constraintAnnotation) {
        try {
            this.entityNames = constraintAnnotation.entities();
        } catch (final Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        // Check for required dependencies
        if (lookupService == null || entityNames == null) {
            throw new RuntimeException("A MasterDataLookupService instance and an entity class must be provided to perform validation.");
        }

        // If field is empty then assume it is valid with respect to the list (unused)
        final String naturalKey = Objects.toString(value, null);
        if (StringUtils.isNotEmpty(naturalKey)) {
            boolean exists = false;
            int i = -1;
            while (!exists && ++i < entityNames.length) {
                exists = lookupService.relaxed().exists(entityNames[i], naturalKey);
            }
            return exists;
        }
        return true;
    }
}
