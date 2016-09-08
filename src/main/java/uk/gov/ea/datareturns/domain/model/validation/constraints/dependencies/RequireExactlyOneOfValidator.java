package uk.gov.ea.datareturns.domain.model.validation.constraints.dependencies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Validator to check that exactly one one field is non-null
 *
 * @author Sam Gardner-Dell
 */
public class RequireExactlyOneOfValidator implements ConstraintValidator<RequireExactlyOneOf, Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequireExactlyOneOfValidator.class);

    private RequireExactlyOneOf annotation;

    @Override
    public void initialize(final RequireExactlyOneOf constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(final Object classInstance, final ConstraintValidatorContext context) {
        try {
            int nonNullCount = 0;
            for (String getterName : this.annotation.fieldGetters()) {
                final Method getter = classInstance.getClass().getDeclaredMethod(getterName);
                final Object fieldValue = getter.invoke(classInstance);
                if (fieldValue != null) {
                    nonNullCount++;
                }
            }
            if (nonNullCount == 1) {
                return true;
            }

            context.disableDefaultConstraintViolation();
            String template = nonNullCount > 1 ? this.annotation.tooManyMessage() : this.annotation.tooFewMessage();
            context.buildConstraintViolationWithTemplate(template).addConstraintViolation();
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }
}
