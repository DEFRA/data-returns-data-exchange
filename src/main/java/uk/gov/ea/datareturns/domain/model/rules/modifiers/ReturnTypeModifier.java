package uk.gov.ea.datareturns.domain.model.rules.modifiers;

/**
 * Created by graham on 19/08/16.
 */
public class ReturnTypeModifier extends EntityModifier {
    @Override
    public Object doModify(Object input) {
        return "Load of Crappy!";
    }
}
