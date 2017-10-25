package uk.gov.ea.datareturns.domain.validation.common.constraints.controlledlist;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.AbstractMasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.service.MasterDataLookupService;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(ControlledListValidator.class);

    private MasterDataLookupService lookupService;

    private Class<? extends AbstractMasterDataEntity>[] entityClasses;

    @Inject public ControlledListValidator(MasterDataLookupService lookupService) {
        this.lookupService = lookupService;
    }

    @Override
    public void initialize(final ControlledList constraintAnnotation) {
        try {
            this.entityClasses = constraintAnnotation.entities();
        } catch (final Throwable t) {
            LOGGER.error(t.getMessage(), t);
        }
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        // Check for required dependencies
        if (lookupService == null || entityClasses == null) {
            throw new RuntimeException("A MasterDataLookupService instance and an entity class must be provided to perform validation.");
        }

        // If field is empty then assume it is valid with respect to the list (unused)
        String naturalKey = Objects.toString(value, null);
        if (StringUtils.isNotEmpty(naturalKey)) {
            boolean exists = false;
            int i = -1;
            while (!exists && ++i < entityClasses.length) {
                exists = lookupService.relaxed().exists(entityClasses[i], naturalKey);
            }
            return exists;
        }
        return true;
    }
}