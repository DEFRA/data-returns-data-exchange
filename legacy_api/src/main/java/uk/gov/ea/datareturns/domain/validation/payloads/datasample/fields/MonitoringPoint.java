package uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.validation.common.entityfields.FieldValue;

/**
 * The monitoring point to which the record relates.
 *
 * @author Sam Gardner-Dell
 */
public class MonitoringPoint implements FieldValue<String> {
    public static final String FIELD_NAME = "Mon_Point";

    @NotBlank(message = "DR9060-Missing")
    @Length(max = 50, message = "DR9060-Length")
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
}
