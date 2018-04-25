package uk.gov.defra.datareturns.validation.constraints.validators;

import lombok.RequiredArgsConstructor;
import uk.gov.defra.datareturns.service.MasterDataNomenclature;
import uk.gov.defra.datareturns.service.csv.EcmCsvRecord;
import uk.gov.defra.datareturns.validation.constraints.annotations.ValidPermitSiteRelationship;
import uk.gov.defra.datareturns.validation.service.MasterDataEntity;
import uk.gov.defra.datareturns.validation.service.MasterDataLookupService;
import uk.gov.defra.datareturns.validation.service.dto.MdSite;
import uk.gov.defra.datareturns.validation.service.dto.MdUniqueIdentifier;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates a site and permit within a particular CSV record do not conflict with each other
 *
 * @author Sam Gardner-Dell
 */
@RequiredArgsConstructor
public class PermitSiteRelationshipValidator implements ConstraintValidator<ValidPermitSiteRelationship, EcmCsvRecord> {
    private final MasterDataLookupService lookupService;

    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(final ValidPermitSiteRelationship constraintAnnotation) {
    }

    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(final EcmCsvRecord record, final ConstraintValidatorContext context) {
        final MdUniqueIdentifier eaId = MasterDataNomenclature.resolveMasterDataEntity(lookupService, MasterDataEntity.UNIQUE_IDENTIFIER,
                MdUniqueIdentifier.class, record.getEaId());
        if (eaId != null) {
            final MdSite siteForEaId = lookupService.get(MdSite.class, eaId.getItemLink(MasterDataEntity.SITE)).orElse(null);
            return siteForEaId != null && MasterDataNomenclature.matches(MasterDataEntity.SITE, siteForEaId.getNomenclature(), record.getSiteName());
        }
        return true;
    }
}
