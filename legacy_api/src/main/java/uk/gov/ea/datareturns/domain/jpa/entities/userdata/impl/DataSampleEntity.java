package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Parameter;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.AbstractPayloadEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * @author Graham Willis
 */
@Entity
@Table(name = "ud_data_sample")
public class DataSampleEntity extends AbstractPayloadEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "ea_id")
    private UniqueIdentifier uniqueIdentifier;

    @ManyToOne(optional = false)
    @JoinColumn(name = "return_type_id")
    private ReturnType returnType;

    @Basic @Column(name = "mon_date", nullable = false)
    private Instant monDate;

    @Basic @Column(name = "mon_point", nullable = false, length = 50)
    private String monPoint;

    @ManyToOne(optional = false)
    @JoinColumn(name = "parameter_id")
    private Parameter parameter;

    @Basic @Column(name = "numeric_value")
    private BigDecimal numericValue;

    @Basic @Column(name = "numeric_value_text", length = 20)
    private String numericValueText;

    @ManyToOne
    @JoinColumn(name = "text_value_id")
    private TextValue textValue;

    @ManyToOne
    @JoinColumn(name = "qualifier_id")
    private Qualifier qualifier;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @ManyToOne
    @JoinColumn(name = "reference_period_id")
    private ReferencePeriod referencePeriod;

    @ManyToOne
    @JoinColumn(name = "method_or_standard_id")
    private MethodOrStandard methodOrStandard;

    @Basic @Column(name = "return_period")
    private String returnPeriod;

    @Basic @Column(name = "comments")
    private String comments;

    public UniqueIdentifier getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(UniqueIdentifier uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public ReturnType getReturnType() {
        return returnType;
    }

    public void setReturnType(ReturnType returnType) {
        this.returnType = returnType;
    }

    public Instant getMonDate() {
        return monDate;
    }

    public void setMonDate(Instant monDate) {
        this.monDate = monDate;
    }

    public String getMonPoint() {
        return monPoint;
    }

    public void setMonPoint(String monPoint) {
        this.monPoint = monPoint;
    }

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

    public String getNumericValueText() {
        return numericValueText;
    }

    public void setNumericValueText(String numericValueText) {
        this.numericValueText = numericValueText;
    }

    public TextValue getTextValue() {
        return textValue;
    }

    public void setTextValue(TextValue textValue) {
        this.textValue = textValue;
    }

    public Qualifier getQualifier() {
        return qualifier;
    }

    public void setQualifier(Qualifier qualifier) {
        this.qualifier = qualifier;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public ReferencePeriod getReferencePeriod() {
        return referencePeriod;
    }

    public void setReferencePeriod(ReferencePeriod referencePeriod) {
        this.referencePeriod = referencePeriod;
    }

    public MethodOrStandard getMethodOrStandard() {
        return methodOrStandard;
    }

    public void setMethodOrStandard(MethodOrStandard methodOrStandard) {
        this.methodOrStandard = methodOrStandard;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getReturnPeriod() {
        return returnPeriod;
    }

    public void setReturnPeriod(String returnPeriod) {
        this.returnPeriod = returnPeriod;
    }

    @Override public String toString() {
        return new ToStringBuilder(this)
                .append("uniqueIdentifier", uniqueIdentifier)
                .append("returnType", returnType)
                .append("monDate", monDate)
                .append("monPoint", monPoint)
                .append("parameter", parameter)
                .append("numericValue", numericValue)
                .append("numericValueText", numericValueText)
                .append("textValue", textValue)
                .append("qualifier", qualifier)
                .append("unit", unit)
                .append("referencePeriod", referencePeriod)
                .append("methodOrStandard", methodOrStandard)
                .append("returnPeriod", returnPeriod)
                .append("comments", comments)
                .toString();
    }
}


