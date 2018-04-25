package uk.gov.defra.datareturns.validation.validators.id;

import lombok.RequiredArgsConstructor;
import uk.gov.defra.datareturns.validation.service.MasterDataLookupService;
import uk.gov.defra.datareturns.validation.service.dto.MdBaseEntity;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validate that the annotated value is an id of a master data entity within one of the provided resource collection URIs
 *
 * @author Sam Gardner-Dell
 */
@RequiredArgsConstructor
public class ValidIdValidator implements ConstraintValidator<ValidId, Object> {
    private final MasterDataLookupService lookupService;
    private Set<String> validIds;

    @Override
    public void initialize(final ValidId constraintAnnotation) {
        validIds = lookupService.list(MdBaseEntity.class, constraintAnnotation.entity().getCollectionLink()).orThrow()
                .stream()
                .map(MasterDataLookupService::getResourceId)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        // Assume true if value is null (a NotNull constraint should be used elsewhere when null is not considered valid)
        return value == null || validIds.contains(Objects.toString(value));
    }
}
