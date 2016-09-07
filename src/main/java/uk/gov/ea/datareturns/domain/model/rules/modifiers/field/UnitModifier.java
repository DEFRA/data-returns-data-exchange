package uk.gov.ea.datareturns.domain.model.rules.modifiers.field;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.UnitDao;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Created by graham on 26/08/16.
 */
@Component
public class UnitModifier implements EntityModifier {
    @Inject
    UnitDao unitDao;

    @Override
    public Object doModify(Object input) {
        return unitDao.getStandardizedName(Objects.toString(input, ""));
    }
}

