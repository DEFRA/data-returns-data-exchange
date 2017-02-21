package uk.gov.ea.datareturns.domain.model.fields.impl;

import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.fields.FieldValue;

/**
 * Reference to the site at which the measurement is being taken.
 *
 * @author Sam Gardner-Dell
 */
public class SiteName implements FieldValue<DataSample, String> {

    @NotBlank(message = MessageCodes.Missing.Site_Name)
    private final String inputValue;

    /**
     * Instantiates a new Site_Name
     *
     * @param inputValue the input value
     */
    public SiteName(String inputValue) {
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return this.inputValue;
    }

    @Override public String getValue() {
        return this.inputValue;
    }

    @Override public String transform(DataSample record) {
        return getValue();
    }
}
