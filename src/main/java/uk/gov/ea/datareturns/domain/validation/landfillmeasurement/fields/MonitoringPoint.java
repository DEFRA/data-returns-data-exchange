package uk.gov.ea.datareturns.domain.validation.landfillmeasurement.fields;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementMvo;
import uk.gov.ea.datareturns.domain.validation.model.MessageCodes;
import uk.gov.ea.datareturns.domain.validation.model.fields.FieldValue;

/**
 * The monitoring point to which the record relates.
 *
 * @author Sam Gardner-Dell
 */
public class MonitoringPoint implements FieldValue<String> {
    @NotBlank(message = MessageCodes.Missing.Mon_Point)
    @Length(max = 50, message = MessageCodes.Length.Mon_Point)
    private String monitoringPoint;

    /**
     * Instantiates a new Mon_Point
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

}
