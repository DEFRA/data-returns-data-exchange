package uk.gov.ea.datareturns.domain.validation.payloads.basicmeasurement.fields;

import uk.gov.ea.datareturns.domain.validation.common.entityfields.FieldValue;
import uk.gov.ea.datareturns.util.TextUtils;

import javax.validation.constraints.Pattern;

/**
 * A numerical value of a measurement.
 */
public class Value implements FieldValue<String> {
    @Pattern(regexp = "([<>]?\\s*-?(\\d+\\.)?(\\d)+)", message = "DR9040-Incorrect")
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
        return TextUtils.normalize(inputValue, TextUtils.WhitespaceHandling.REMOVE);
    }

}
