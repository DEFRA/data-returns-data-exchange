package uk.gov.ea.datareturns.domain.model.rules.modifiers;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.QualifierDao;

import javax.inject.Inject;

/**
 * Created by graham on 22/08/16.
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
