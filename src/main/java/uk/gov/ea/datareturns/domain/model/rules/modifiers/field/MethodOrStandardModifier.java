package uk.gov.ea.datareturns.domain.model.rules.modifiers.field;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.MethodOrStandardDao;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Graham Willis: 23/08/16
 * Perform the standard name modification on the method or standard
 */
@Component
public class MethodOrStandardModifier implements EntityModifier {

    @Inject
    private MethodOrStandardDao methodOrStandardDao;

    public Object doModify(Object input) {
        return methodOrStandardDao.getStandardizedName(Objects.toString(input, ""));
    }
}
