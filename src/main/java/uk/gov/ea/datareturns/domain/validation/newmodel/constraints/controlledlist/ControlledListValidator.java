package uk.gov.ea.datareturns.domain.validation.newmodel.constraints.controlledlist;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

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

    /** ControlledListProvider instance to provide the list of values we must validate against */
    private ControlledListAuditor provider;

    @Inject
    private ApplicationContext applicationContext;

    @Override
    public void initialize(final ControlledList constraintAnnotation) {
        try {
            final Class<? extends ControlledListAuditor> providerType = constraintAnnotation.auditor();
            this.provider = this.applicationContext.getBean(providerType);
        } catch (final Throwable t) {
            LOGGER.error(t.getMessage(), t);
        }
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        boolean valid = false;
        if (StringUtils.isEmpty(Objects.toString(value, ""))) {
            // If field is empty then this is valid.
            valid = true;
        } else {
            // Assume item is valid if there is no list to validate against.
            if (this.provider != null) {
                try {
                    valid = this.provider.isValid(value);
                } catch (final Throwable t) {
                    LOGGER.error("Unable to perform Controlled List Validation", t);
                    throw t;
                }
            }
        }
        return valid;
    }
}
