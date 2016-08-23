package uk.gov.ea.datareturns.domain.model.rules.modifiers;

/**
 * Graham Willis: 23/08/16
 * The default modifier for the @EntityModifier annotation does nothing
 */

public class NullModifier implements EntityModifier {
    public Object doModify(Object input) {
        return input;
    }
}
