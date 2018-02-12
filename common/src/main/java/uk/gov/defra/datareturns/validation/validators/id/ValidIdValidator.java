package uk.gov.defra.datareturns.validation.validators.id;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotationUtils;
import uk.gov.defra.datareturns.validation.service.ValidationCacheService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * Validate that the annotated value is an id of a master data entity within one of the provided resource collection URIs
 *
 * @author Sam Gardner-Dell
 */
@RequiredArgsConstructor
public class ValidIdValidator implements ConstraintValidator<ValidId, Object> {
    private final ValidationCacheService cacheService;
    private String[] resourceUris;

    @Override
    public void initialize(final ValidId constraintAnnotation) {
        this.resourceUris = constraintAnnotation.resourceCollectionUris();
        if (this.resourceUris.length == 0) {
            throw new RuntimeException("One or more resource URIs must be provided to perform validation.");
        }
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        boolean exists = false;
        int i = -1;
        while (!exists && ++i < resourceUris.length) {
            exists = cacheService.getResourceNomenclatureMap(resourceUris[i]).containsKey(Objects.toString(value));
        }
        return exists;
    }
}
