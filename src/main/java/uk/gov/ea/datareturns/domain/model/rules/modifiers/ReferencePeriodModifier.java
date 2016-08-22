package uk.gov.ea.datareturns.domain.model.rules.modifiers;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.ReferencePeriodDao;

import javax.inject.Inject;

/**
 * Created by graham on 22/08/16.
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