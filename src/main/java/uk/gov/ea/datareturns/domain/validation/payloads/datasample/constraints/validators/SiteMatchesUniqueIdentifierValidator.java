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

        // If we have no site name we need to just OK it. The site name validation
        // checks for not-blank so we don't want to report this as well
        // This gives an unfortunate dependency between the two validations
        // which is unavoidable at this stage. It should really check against the controlled list
        // but cannot do so while being case-insensitive. (There are collisions)
        if (site.getInputValue() == null) {
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