package uk.gov.ea.datareturns.domain.model.fields.impl;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.fields.FieldValue;

import javax.validation.constraints.Pattern;

/**
 * The monitoring point to which the record relates.
 *
 * @author Sam Gardner-Dell
 */
public class MonitoringPoint implements FieldValue<DataSample, String> {
    @NotBlank(message = "{DR9060-Missing}")
    @Length(max = 30, message = "{DR9060-Length}")
    @Pattern(regexp = REGEX_SIMPLE_TEXT, message = "{DR9060-Incorrect}")
    private String monitoringPoint;

    /**
     * Instantiates a new MonitoringPoint
     *
     * @param monitoringPoint the monitoring point
     */
    public MonitoringPoint(String monitoringPoint) {
        this.monitoringPoint = monitoringPoint;
    }

    @Override public String getInputValue() {
        return this.monitoringPoint;
    }

    @Override public String getValue() {
        return this.monitoringPoint;
    }

    @Override public String transform(DataSample record) {
        return getValue();
    }
}
