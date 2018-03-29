package uk.gov.defra.datareturns.validation.constraints.validators;

import uk.gov.defra.datareturns.data.model.record.Record;
import uk.gov.defra.datareturns.service.MasterDataValidationService;
import uk.gov.defra.datareturns.validation.constraints.annotations.SiteMatchesUniqueIdentifier;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by sam on 11/11/16.
 */
public class SiteMatchesUniqueIdentifierValidator implements ConstraintValidator<SiteMatchesUniqueIdentifier, Record> {
    private final MasterDataValidationService lookupService;

    @Inject
    public SiteMatchesUniqueIdentifierValidator(final MasterDataValidationService lookupService) {
        this.lookupService = lookupService;
    }

    @Override
    public void initialize(final SiteMatchesUniqueIdentifier siteMatchesUniqueIdentifier) {

    }

    @Override
    public boolean isValid(final Record record, final ConstraintValidatorContext context) {
        // FIXME - Re-enable validation
//        final EaId eaId = record.getEaId();
//
//        // Attempt to resolve against an alias
//        UniqueIdentifier eaIdEntity = null;
//        final UniqueIdentifierAlias aliasEntity = this.lookupService.relaxed().find(UniqueIdentifierAlias.class, eaId.getInputValue());
//        if (aliasEntity != null) {
//            eaIdEntity = aliasEntity.getPreferred();
//        }
//
//        // If not found for an alias, resolve against the primary list
//        if (eaIdEntity == null) {
//            eaIdEntity = this.lookupService.relaxed().find(UniqueIdentifier.class, eaId.getInputValue());
//        }
//
//        final SiteName site = record.getSiteName();
//        if (StringUtils.isEmpty(site.getInputValue())) {
//            return true;
//        }
//        final Site siteEntity = this.lookupService.relaxed().find(Site.class, site.getInputValue());
//
//        if (eaIdEntity != null && !eaIdEntity.getSite().equals(siteEntity)) {
//            context.disableDefaultConstraintViolation();
//            context.buildConstraintViolationWithTemplate("DR9110-Conflict").addConstraintViolation();
//            return false;
//        }
        return true;
    }
}
