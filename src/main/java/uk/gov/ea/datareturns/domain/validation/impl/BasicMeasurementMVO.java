package uk.gov.ea.datareturns.domain.validation.impl;

import uk.gov.ea.datareturns.domain.dto.impl.BasicMeasurementDto;
import uk.gov.ea.datareturns.domain.model.fields.impl.Parameter;
import uk.gov.ea.datareturns.domain.model.validation.constraints.factory.ValidRecord;
import uk.gov.ea.datareturns.domain.validation.MVO;

import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * @author Graham Willis
 * Object contaioning fields and hibernate validation annotations
 */
@ValidRecord(value = BasicMeasurementMVO.class)
public class BasicMeasurementMVO extends MVO<BasicMeasurementDto> {

    @Valid private Parameter parameter;
    @Valid private BigDecimal value;

    /**
     * Initialize with a data transport object DTO
     *
     * @param dto
     */
    public BasicMeasurementMVO(BasicMeasurementDto dto) {
        super(dto);

        // Initialize the fields for validation
        parameter = new Parameter(this.dto.getParameter());
        value = this.dto.getValue();
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
