package uk.gov.defra.datareturns.validation.payloads.datasample.fields;

import javax.validation.constraints.Pattern;

/**
 * A numerical value of a measurement.
 */
public class Value {
    public static final String FIELD_NAME = "Value";

    @Pattern(regexp = "([<>]?\\s*-?(\\d+\\.)?(\\d)+)", message = "DR9040-Incorrect")
    private final String inputValue;

    /**
     * Instantiates a new Value.
     *
     * @param inputValue the input value
     */
    public Value(final String inputValue) {
        this.inputValue = inputValue;
    }

    public String getInputValue() {
        return inputValue;
    }
}
