package uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields;

import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.TextValue;
import uk.gov.ea.datareturns.domain.validation.common.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.common.entityfields.FieldValue;

/**
 * Models measurements/observations returned as text such as true, false, yes and no
 *
 * @author Sam Gardner-Dell
 */
public class TxtValue implements FieldValue<TextValue> {
    public static final String FIELD_NAME = "Txt_Value";
    @ControlledList(entities = uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.TextValue.class, message = "DR9080-Incorrect")
    private final String inputValue;

    /**
     * Instantiates a new Txt_Value.
     *
     * @param inputValue the input value
     */
    public TxtValue(String inputValue) {
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}
