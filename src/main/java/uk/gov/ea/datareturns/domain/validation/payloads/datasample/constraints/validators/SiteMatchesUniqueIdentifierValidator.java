package uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.validators;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.DataSampleValidationObject;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.annotations.SiteMatchesUniqueIdentifier;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.EaId;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.SiteName;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by sam on 11/11/16.
 */
public class SiteMatchesUniqueIdentifierValidator implements ConstraintValidator<SiteMatchesUniqueIdentifier, DataSampleValidationObject> {

    @Override
    public void initialize(SiteMatchesUniqueIdentifier siteMatchesUniqueIdentifier) {

    }

    @Override
    public boolean isValid(DataSampleValidationObject record, final ConstraintValidatorContext context) {
        final EaId eaId = record.getEaId();
        final UniqueIdentifier eaIdEntity = eaId.getEntity();
        final SiteName site = record.getSiteName();

        if (StringUtils.isEmpty(site.getInputValue())) {
            return true;
        }

        if (eaIdEntity != null && !StringUtils.equalsIgnoreCase(eaId.getEntity().getSite().getName(), site.getValue())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("DR9110-Conflict").addConstraintViolation();
            return false;
        }
        return true;
    }
}
