package uk.gov.ea.datareturns.domain.model.rules.modifiers;

/**
 * Created by graham on 19/08/16.
 */
public interface EntityModifier {
    Object doModify(Object input);
}
