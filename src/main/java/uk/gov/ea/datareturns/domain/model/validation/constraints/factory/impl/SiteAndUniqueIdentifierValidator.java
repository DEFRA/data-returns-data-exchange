package uk.gov.ea.datareturns.domain.model.validation.constraints.factory.impl;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.fields.impl.EaId;
import uk.gov.ea.datareturns.domain.model.fields.impl.SiteName;
import uk.gov.ea.datareturns.domain.model.validation.constraints.factory.RecordConstraintValidator;

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
        if (eaIdEntity != null && !StringUtils.equalsIgnoreCase(eaId.getEntity().getSite().getName(), site.getValue())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MessageCodes.Conflict.UniqueIdentifierSiteConflict).addConstraintViolation();
            return false;
        }
        return true;
    }
}
