package uk.gov.ea.datareturns.domain.model.fields.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.fields.FieldValue;
import uk.gov.ea.datareturns.util.TextUtils;

import javax.validation.constraints.Pattern;

/**
 * A numerical value of a measurement. As well as numbers, can include the symbols < or >
 */
public class Value implements FieldValue<DataSample, String> {
    @Pattern(regexp = "([<>]?\\s*-?(\\d+\\.)?(\\d)+)", message = MessageCodes.Incorrect.Value)
    private final String inputValue;

    /**
     * Instantiates a new Value.
     *
     * @param inputValue the input value
     */
    @JsonCreator
    public Value(String inputValue) {
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }

    @Override public String getValue() {
        return TextUtils.normalize(inputValue, TextUtils.WhitespaceHandling.REMOVE);
    }

    @Override public String transform(DataSample record) {
        return getValue();
    }
}
