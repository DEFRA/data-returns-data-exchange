package uk.gov.ea.datareturns.domain.validation.landfillmeasurement.fields;

import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementMvo;
import uk.gov.ea.datareturns.domain.validation.model.MessageCodes;
import uk.gov.ea.datareturns.domain.validation.model.fields.FieldValue;

/**
 * Reference to the site at which the measurement is being taken.
 *
 * @author Sam Gardner-Dell
 */
public class SiteName implements FieldValue<LandfillMeasurementMvo, String> {

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

    @Override public String transform(LandfillMeasurementMvo record) {
        return getValue();
    }
}
