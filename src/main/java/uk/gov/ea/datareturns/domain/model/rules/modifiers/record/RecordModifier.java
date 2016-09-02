package uk.gov.ea.datareturns.domain.model.rules.modifiers.record;

import uk.gov.ea.datareturns.domain.io.csv.generic.AbstractCSVRecord;

/**
 * Created by graham on 01/09/16.
 */
public interface RecordModifier<T extends AbstractCSVRecord> {
    void doProcess(T record);
}
