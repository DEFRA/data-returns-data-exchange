package uk.gov.ea.datareturns.domain.model.validation.constraints.dependencies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Sam Gardner-Dell
 *
 */
public class DependentFieldValidator implements ConstraintValidator<DependentField, Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DependentFieldValidator.class);

    private DependentFieldAuditor auditor;

    private String primaryFieldGetterName;

    private String dependentFieldGetterName;

    @Override
    public void initialize(final DependentField constraintAnnotation) {
        this.primaryFieldGetterName = constraintAnnotation.primaryFieldGetter();
        this.dependentFieldGetterName = constraintAnnotation.dependentFieldGetter();
        try {
            LOGGER.info(String.format("Setting up dependent field validation between %s and %s using auditor %s", primaryFieldGetterName,
                    dependentFieldGetterName, constraintAnnotation.auditor().getName()));
            final Class<? extends DependentFieldAuditor> providerType = constraintAnnotation.auditor();
            this.auditor = providerType.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            LOGGER.error("Failed to set up dependent field validator", e);

        }
    }

    @Override
    public boolean isValid(final Object classInstance, final ConstraintValidatorContext context) {
        try {
            final Method primaryFieldGetter = classInstance.getClass().getDeclaredMethod(this.primaryFieldGetterName);
            final Method dependentFieldGetter = classInstance.getClass().getDeclaredMethod(this.dependentFieldGetterName);

            final Object primaryFieldValue = primaryFieldGetter.invoke(classInstance);
            final Object dependentFieldValue = dependentFieldGetter.invoke(classInstance);

            return this.auditor.isValid(primaryFieldValue, dependentFieldValue);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return true;
    }
}
