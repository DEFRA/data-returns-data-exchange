package uk.gov.ea.datareturns.domain.model.rules.modifiers.field;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.TextValueDao;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Created by graham on 30/08/16.
 */
@Component
public class TextValueModifier implements EntityModifier {
    @Inject
    private TextValueDao dao;

    @Override
    public Object doModify(Object input) {
        return dao.getStandardizedName(Objects.toString(input, ""));
    }
}
