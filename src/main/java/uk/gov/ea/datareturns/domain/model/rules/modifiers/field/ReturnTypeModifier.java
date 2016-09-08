package uk.gov.ea.datareturns.domain.model.rules.modifiers.field;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.ReturnTypeDao;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Graham Willis: 23/08/16
 * Perform the standard name modification on the return type
 */
@Component
public class ReturnTypeModifier implements EntityModifier {
    @Inject
    ReturnTypeDao returnTypeDao;

    @Override
    public Object doModify(Object input) {
        return returnTypeDao.getStandardizedName(Objects.toString(input, ""));
    }
}
