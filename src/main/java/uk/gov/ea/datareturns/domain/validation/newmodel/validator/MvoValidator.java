package uk.gov.ea.datareturns.domain.validation.newmodel.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.impl.ValidationErrorDao;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.ValidationError;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Graham Willis
 */
public class MvoValidator implements ObservationValidator<Mvo> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MvoValidator.class);

    /** validator instance */
    private final Validator instanceValidator;
    private final ValidationErrorDao validationErrorDao;
    /** flag to indicate if the instanceValidator has been fully initialised */
    private boolean initialised = false;

    /**
     * Instantiates a new {@link Mvo} instanceValidator.
     *
     * @param validator the hibernate instanceValidator instance
     */
    @Inject
    public MvoValidator(final Validator validator, ValidationErrorDao validationErrorDao) {
        this.instanceValidator = validator;
        this.validationErrorDao = validationErrorDao;
    }

    public Set<ValidationError> validateObservation(Mvo mvo) {
        final Set<ValidationError> errors = new HashSet<>();
        final Set<ConstraintViolation<Mvo>> violations = validate(mvo);
        for (final ConstraintViolation<Mvo> violation : violations) {
            ValidationError error = validationErrorDao.get(violation.getMessageTemplate());
            if (error == null) {
                LOGGER.error("Unknown message template discovered: " + violation.getMessageTemplate());
            } else {
                errors.add(error);
            }
        }

        return errors;
    }

    /**
     * Wrapper to handle synchronisation of the hibernate instanceValidator initialisation.
     * This is necessary because when multiple threads call this.instanceValidator.validate(..) before it is completely initialised, then the
     * initialisation sequence may get run more than once.  This results in any referenced
     * {@link javax.validation.ConstraintValidator#initialize(Annotation)} method being called more than once.
     *
     * @param mvo the measurement to be validated
     * @return a set of constraint violations detailing any validation errors that were found
     */
    private Set<ConstraintViolation<Mvo>> validate(Mvo mvo) {
        Set<ConstraintViolation<Mvo>> violations;
        if (initialised) {
            // Avoid synchronisation if initialised
            violations = this.instanceValidator.validate(mvo);
        } else {
            // Synchronise first call to instanceValidator if not yet initialised.
            synchronized (this) {
                violations = this.instanceValidator.validate(mvo);
            }
            initialised = true;
        }
        return violations;
    }
}
