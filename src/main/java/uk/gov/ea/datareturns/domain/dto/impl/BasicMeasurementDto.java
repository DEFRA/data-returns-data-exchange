package uk.gov.ea.datareturns.domain.dto.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.ea.datareturns.domain.dto.MeasurementDto;

import java.math.BigDecimal;

/**
 * @Author Graham Willis
 * Basic measurement class (prototype)
 */
public class BasicMeasurementDto implements MeasurementDto {
    @JsonProperty("Parameter")
    private String parameter;

    @JsonProperty("Value")
    private String value;

    public String getParameter() {
        return parameter;
    }
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "BasicMeasurementDto{" +
                "parameter='" + parameter + '\'' +
                ", value=" + value +
                '}';
    }
}
