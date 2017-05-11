package uk.gov.ea.datareturns.domain.validation.landfillmeasurement.fields;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementMvo;
import uk.gov.ea.datareturns.domain.validation.model.MessageCodes;
import uk.gov.ea.datareturns.domain.validation.model.fields.FieldValue;

/**
 * Free-text comments about the row of data
 *
 * @author Sam Gardner-Dell
 */
public class Comments implements FieldValue<LandfillMeasurementMvo, String> {
    @Length(max = 255, message = MessageCodes.Length.Comments)
    private final String inputValue;

    /**
     * Instantiates a new Comments
     *
     * @param inputValue the input value
     */
    public Comments(String inputValue) {
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }

    @Override public String getValue() {
        return inputValue;
    }

    @Override public String transform(LandfillMeasurementMvo record) {
        return StringUtils.trim(getValue());
    }
}
