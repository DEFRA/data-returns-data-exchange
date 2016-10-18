package uk.gov.ea.datareturns.domain.model.fields.impl;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.fields.FieldValue;

import javax.validation.constraints.Pattern;

/**
 * Reference to the site at which the measurement is being taken. This field allows EA to cross check site name against EA_ID.
 *
 * @author Sam Gardner-Dell
 */
public class SiteName implements FieldValue<DataSample, String> {
    @Pattern(regexp = REGEX_SIMPLE_TEXT, message = "{DR9110-Incorrect}")
    @Length(max = 255, message = "{DR9110-Length}")
    @NotBlank(message = "{DR9110-Missing}")
    private final String inputValue;

    /**
     * Instantiates a new SiteName
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
