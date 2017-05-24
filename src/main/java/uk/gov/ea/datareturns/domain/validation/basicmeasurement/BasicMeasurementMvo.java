package uk.gov.ea.datareturns.domain.validation.basicmeasurement;

import uk.gov.ea.datareturns.domain.dto.impl.BasicMeasurementDto;
import uk.gov.ea.datareturns.domain.validation.basicmeasurement.fields.*;
import uk.gov.ea.datareturns.domain.validation.newmodel.validator.Mvo;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;

import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * @author Graham Willis
 * Object contaioning entityfields and hibernate validation annotations
 */
public class BasicMeasurementMvo extends Mvo {

    @Valid private Parameter parameter;
    @Valid private BigDecimal value;

    /**
     * Initialize with a data transport object DTO
     *
     * @param payload
     */
    public BasicMeasurementMvo(Payload payload) {
        super(payload);

        // Initialize the entityfields for validation
        //parameter = new Parameter(dto.getParameter());
        //value = new BigDecimal(dto.getValue());
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
