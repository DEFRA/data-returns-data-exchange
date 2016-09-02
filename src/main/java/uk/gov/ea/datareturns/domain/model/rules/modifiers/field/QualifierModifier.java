package uk.gov.ea.datareturns.domain.model.rules.modifiers.field;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.QualifierDao;

import javax.inject.Inject;

/**
 * Graham Willis: 23/08/16
 * Perform the standard name modification on the qualifier
 */
@Component
public class QualifierModifier implements EntityModifier {
    @Inject
    QualifierDao returnTypeDao;

    @Override
    public Object doModify(Object input) {
        return returnTypeDao.getStandardizedName(input.toString());
    }
}
