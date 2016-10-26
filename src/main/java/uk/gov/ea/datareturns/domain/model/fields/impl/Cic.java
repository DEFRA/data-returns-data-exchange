package uk.gov.ea.datareturns.domain.model.fields.impl;

import org.hibernate.validator.constraints.Length;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.fields.FieldValue;

/**
 * The type CiC to store Commercial in Confidence flag.
 *
 * @author Sam Gardner-Dell
 */
public class Cic implements FieldValue<DataSample, String> {
    @Length(max = 255, message = MessageCodes.Length.CiC)
    private final String inputValue;

    /**
     * Instantiates a new CiC.
     *
     * @param inputValue the input value
     */
    public Cic(String inputValue) {
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }

    @Override public String getValue() {
        return inputValue;
    }

    @Override public String transform(DataSample record) {
        return this.inputValue;
    }
}
