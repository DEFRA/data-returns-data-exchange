package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Parameter;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractMeasurement;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author Graham Willis
 */
@Entity
@Table(name = "measurements")
public class BasicMeasurement extends AbstractMeasurement {

    @ManyToOne(optional = false)
    @JoinColumn(name = "parameter_id", referencedColumnName = "id")
    Parameter parameter;

    @Basic @Column(name = "value")
    BigDecimal numericValue;

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    public BigDecimal getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(BigDecimal numericValue) {
        this.numericValue = numericValue;
    }
}
