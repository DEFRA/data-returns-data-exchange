package uk.gov.ea.datareturns.domain.validation;

import uk.gov.ea.datareturns.domain.validation.model.fields.FieldValue;
import uk.gov.ea.datareturns.domain.validation.model.rules.FieldDefinition;

import java.util.List;

/**
 * @author Graham Willis
 */
public interface FieldMessageMap<V extends Mvo> {
    /**
     * Relates an error message to a set of FieldValue items which are added to the error validation message
     * as part of the validation.
     * @param message
     * @return
     */
    List<FieldValue<V, ?>> getFieldDependencies(V measurement, String message);

}
