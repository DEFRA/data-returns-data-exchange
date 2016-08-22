package uk.gov.ea.datareturns.domain.model.rules.modifiers;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.ReturnTypeDao;

import javax.inject.Inject;

/**
 * Created by graham on 19/08/16.
 */
@Component
public class ReturnTypeModifier implements EntityModifier {
    @Inject
    ReturnTypeDao returnTypeDao;

    @Override
    public Object doModify(Object input) {
        return returnTypeDao.getStandardizedName(input.toString());
    }
}
