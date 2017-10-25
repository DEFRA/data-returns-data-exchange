package uk.gov.ea.datareturns.domain.validation.common.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.PayloadType;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.ValidationError;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.ValidationErrorId;
import uk.gov.ea.datareturns.domain.jpa.repositories.systemdata.PayloadTypeRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.systemdata.ValidationConstraintRepository;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Graham Willis
 */
public class ValidatorImpl implements Validator<AbstractValidationObject> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorImpl.class);

    /** validator instance */
    private final javax.validation.Validator instanceValidator;
    private final ValidationConstraintRepository validationConstraintRepository;
    private final PayloadTypeRepository payloadTypeRepository;
    /** flag to indicate if the instanceValidator has been fully initialised */
    private boolean initialised = false;

    /**
     * Instantiates a new {@link AbstractValidationObject} instanceValidator.
     *
     * @param validator the hibernate instanceValidator instance
     */
    @Inject
    public ValidatorImpl(final javax.validation.Validator validator, ValidationConstraintRepository validationConstraintRepository, PayloadTypeRepository payloadTypeRepository) {
        this.instanceValidator = validator;
        this.validationConstraintRepository = validationConstraintRepository;
        this.payloadTypeRepository = payloadTypeRepository;
    }

    public Set<ValidationError> validateValidationObject(AbstractValidationObject validationObject) {
        final Set<ValidationError> errors = new HashSet<>();

        PayloadType payloadType = payloadTypeRepository.getOne((validationObject).payload.getPayloadType());
        final Set<ConstraintViolation<AbstractValidationObject>> violations = validate(validationObject);
        for (final ConstraintViolation<AbstractValidationObject> violation : violations) {
            ValidationErrorId id = new ValidationErrorId();
            id.setError(violation.getMessageTemplate());
            id.setPayloadType(payloadType);
            ValidationError error = validationConstraintRepository.getOne(id);
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
    private Set<ConstraintViolation<AbstractValidationObject>> validate(AbstractValidationObject mvo) {
        Set<ConstraintViolation<AbstractValidationObject>> violations;
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
