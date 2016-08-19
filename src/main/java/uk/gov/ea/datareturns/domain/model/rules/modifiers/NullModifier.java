package uk.gov.ea.datareturns.domain.model.rules.modifiers;

/**
 * Created by graham on 19/08/16.
 */
public class NullModifier extends EntityModifier {
    public Object doModify(Object input) {
        return input;
    }
}
