package uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.validators;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierAlias;
import uk.gov.ea.datareturns.domain.jpa.service.MasterDataLookupService;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.DataSampleValidationObject;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.annotations.SiteMatchesUniqueIdentifier;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.EaId;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.SiteName;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by sam on 11/11/16.
 */
public class SiteMatchesUniqueIdentifierValidator implements ConstraintValidator<SiteMatchesUniqueIdentifier, DataSampleValidationObject> {
    private final MasterDataLookupService lookupService;

    @Inject public SiteMatchesUniqueIdentifierValidator(MasterDataLookupService lookupService) {
        this.lookupService = lookupService;
    }

    @Override
    public void initialize(SiteMatchesUniqueIdentifier siteMatchesUniqueIdentifier) {

    }

    @Override
    public boolean isValid(DataSampleValidationObject record, final ConstraintValidatorContext context) {
        final EaId eaId = record.getEaId();

        // Attempt to resolve against an alias
        UniqueIdentifier eaIdEntity = null;
        final UniqueIdentifierAlias aliasEntity = this.lookupService.relaxed().find(UniqueIdentifierAlias.class, eaId.getInputValue());
        if (aliasEntity != null) {
            eaIdEntity = aliasEntity.getPreferred();
        }

        // If not found for an alias, resolve against the primary list
        if (eaIdEntity == null) {
            eaIdEntity = this.lookupService.relaxed().find(UniqueIdentifier.class, eaId.getInputValue());
        }

        final SiteName site = record.getSiteName();
        if (StringUtils.isEmpty(site.getInputValue())) {
            return true;
        }
        final Site siteEntity = this.lookupService.relaxed().find(Site.class, site.getInputValue());

        if (eaIdEntity != null && !eaIdEntity.getSite().equals(siteEntity)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("DR9110-Conflict").addConstraintViolation();
            return false;
        }
        return true;
    }
}
