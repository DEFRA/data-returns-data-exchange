package uk.gov.ea.datareturns.domain.validation.newmodel.constraints.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by sam on 12/10/16.
 */
public class RecordValidatorFactory implements ConstraintValidator<ValidRecord, Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordValidatorFactory.class);
    private Set<RecordConstraintValidator<Object>> validators = new HashSet<>();

    @Override
    public final void initialize(ValidRecord constraintAnnotation) {
        this.validators = buildValidators(constraintAnnotation.value());
    }

    protected Set<RecordConstraintValidator<Object>> buildValidators(Class<?> forType) {
        Set<RecordConstraintValidator<Object>> validators = new HashSet<>();
        final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(RecordConstraintValidator.class));
        final Set<BeanDefinition> result = scanner.findCandidateComponents("uk.gov.ea.datareturns");
        for (final BeanDefinition defintion : result) {
            try {
                Class<RecordConstraintValidator<Object>> validatorCls = (Class<RecordConstraintValidator<Object>>) Class
                        .forName(defintion.getBeanClassName());

                for (Type interfaceType : validatorCls.getGenericInterfaces()) {
                    if (interfaceType instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) interfaceType;
                        Optional<Type> actualType = Arrays.stream(parameterizedType.getActualTypeArguments()).findFirst();
                        if (actualType.isPresent() && actualType.get().getTypeName().equals(forType.getName())) {
                            LOGGER.info("Initialised validator " + validatorCls.getName());
                            validators.add(validatorCls.newInstance());
                        }
                    }
                }
            } catch (ReflectiveOperationException e) {
                LOGGER.error("Unable to load class for bean definition.", e);
            }
        }
        return validators;
    }

    @Override
    public final boolean isValid(final Object record, final ConstraintValidatorContext context) {
        boolean isValid = true;
        for (RecordConstraintValidator<Object> v : validators) {
            isValid = v.isValid(record, context) && isValid;
        }
        return isValid;
    }
}