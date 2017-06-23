package uk.gov.ea.datareturns.domain.model.validation.constraints.factory.impl;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.fields.impl.EaId;
import uk.gov.ea.datareturns.domain.model.fields.impl.SiteName;
import uk.gov.ea.datareturns.domain.model.validation.constraints.factory.RecordConstraintValidator;
import uk.gov.ea.datareturns.util.TextUtils;

import javax.validation.ConstraintValidatorContext;

/**
 * Created by sam on 11/11/16.
 */
public class SiteAndUniqueIdentifierValidator implements RecordConstraintValidator<DataSample> {

    @Override
    public boolean isValid(DataSample record, final ConstraintValidatorContext context) {
        final EaId eaId = record.getEaId();
        final UniqueIdentifier eaIdEntity = eaId.getEntity();
        final SiteName site = record.getSiteName();

        // If we have no site name we need to just OK it. The site name validation
        // checks for not-blank so we don't want to report this as well
        // This gives an unfortunate dependency between the two validations
        // which is unavoidable at this stage. It should really check against the controlled list
        // but cannot do so while being case-insensitive. (There are collisions)
        if (eaIdEntity != null) {

            final String inputSiteName = TextUtils.normalize(site.getInputValue());
            final String foundSiteName = TextUtils.normalize(eaId.getEntity().getSite().getName());

            if (inputSiteName == null) {
                return true;
            }

            if (!StringUtils.equalsIgnoreCase(foundSiteName, inputSiteName)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(MessageCodes.Conflict.UniqueIdentifierSiteConflict).addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}