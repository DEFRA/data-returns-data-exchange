package uk.gov.ea.datareturns.domain.model.fields.impl;

import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.fields.FieldValue;

import javax.validation.constraints.Pattern;

/**
 * A numerical value of a measurement. As well as numbers, can include the symbols < or >
 */
public class Value implements FieldValue<DataSample, String> {
    @Pattern(regexp = "([<>]?-?(\\d+\\.)?(\\d)+)", message = MessageCodes.Incorrect.Value)
    private final String inputValue;

    /**
     * Instantiates a new Value.
     *
     * @param inputValue the input value
     */
    public Value(String inputValue) {
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }

    @Override public String getValue() {
        return inputValue;
    }

    @Override public String transform(DataSample record) {
        return getValue();
    }
}
