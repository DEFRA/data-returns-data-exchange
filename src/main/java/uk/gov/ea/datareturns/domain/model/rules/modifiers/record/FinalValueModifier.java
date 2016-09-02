package uk.gov.ea.datareturns.domain.model.rules.modifiers.record;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.model.DataSample;

/**
 * Created by graham on 01/09/16.
 */
@Component
public class FinalValueModifier implements RecordModifier<DataSample> {
    @Override
    public void doProcess(DataSample record) {
        if (record.getValue() == null || record.getValue().isEmpty()) {
            record.setValue(record.getTextValue());
        }
    }
}
