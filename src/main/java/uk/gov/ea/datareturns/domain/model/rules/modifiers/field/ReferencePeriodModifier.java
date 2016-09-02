package uk.gov.ea.datareturns.domain.model.rules.modifiers.field;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.ReferencePeriodDao;

import javax.inject.Inject;

/**
 * Graham Willis: 23/08/16
 * Perform the standard name modification on the reference period
 */
@Component
public class ReferencePeriodModifier implements EntityModifier {
    @Inject
    ReferencePeriodDao referencePeriodDao;

    @Override
    public Object doModify(Object input) {
        return referencePeriodDao.getStandardizedName(input.toString());
    }
}