package uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl;

import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Parameter;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.Submission;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Graham Willis
 */
@Entity
@Table(name = "submissions")
public class DataSampleSubmission extends Submission {

    @ManyToOne(optional = false)
    @JoinColumn(name = "ea_id", referencedColumnName = "id")
    UniqueIdentifier uniqueIdentifier;

    @ManyToOne(optional = false)
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    Site site;

    @ManyToOne(optional = false)
    @JoinColumn(name = "return_type_id", referencedColumnName = "id")
    ReturnType returnType;

    @Basic @Column(name = "mon_date", nullable = false)
    private Date monDate;

    @Basic @Column(name = "mon_point", nullable = false, length = 50)
    private String monPoint;

    @ManyToOne(optional = false)
    @JoinColumn(name = "parameter_id", referencedColumnName = "id")
    Parameter parameter;

    @Basic @Column(name = "numeric_value")
    BigDecimal numericValue;

    @Basic @Column(name = "numeric_value_text", length = 20)
    String numericValueText;

    @ManyToOne
    @JoinColumn(name = "text_value_id", referencedColumnName = "id")
    TextValue textValue;

    @ManyToOne
    @JoinColumn(name = "qualifier_id", referencedColumnName = "id")
    Qualifier qualifier;

    @ManyToOne
    @JoinColumn(name = "unit_id", referencedColumnName = "id")
    Unit unit;

    @ManyToOne
    @JoinColumn(name = "reference_period_id", referencedColumnName = "id")
    ReferencePeriod referencePeriod;

    @ManyToOne
    @JoinColumn(name = "method_or_standard_id", referencedColumnName = "id")
    MethodOrStandard methodOrStandard;

    @Basic @Column(name = "comments", length = 255)
    String comments;

    @Basic @Column(name = "cic", length = 255)
    String cic;

    @ManyToOne
    @JoinColumn(name = "releases_and_transfers_id", referencedColumnName = "id")
    ReleasesAndTransfers releasesAndTransfers;

    public UniqueIdentifier getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(UniqueIdentifier uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public ReturnType getReturnType() {
        return returnType;
    }

    public void setReturnType(ReturnType returnType) {
        this.returnType = returnType;
    }

    public Date getMonDate() {
        return monDate;
    }

    public void setMonDate(Date monDate) {
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

    public String getCic() {
        return cic;
    }

    public void setCic(String cic) {
        this.cic = cic;
    }

    public ReleasesAndTransfers getReleasesAndTransfers() {
        return releasesAndTransfers;
    }

    public void setReleasesAndTransfers(ReleasesAndTransfers releasesAndTransfers) {
        this.releasesAndTransfers = releasesAndTransfers;
    }

    @Override
    public String toString() {
        return "DataSampleSubmission{" +
                "uniqueIdentifier=" + uniqueIdentifier +
                ", site=" + site +
                ", returnType=" + returnType +
                ", monDate=" + monDate +
                ", monPoint='" + monPoint + '\'' +
                ", parameter=" + parameter +
                ", numericValue=" + numericValue +
                ", numericValueText='" + numericValueText + '\'' +
                ", textValue=" + textValue +
                ", qualifier=" + qualifier +
                ", unit=" + unit +
                ", referencePeriod=" + referencePeriod +
                ", methodOrStandard=" + methodOrStandard +
                ", comments='" + comments + '\'' +
                ", cic='" + cic + '\'' +
                ", releasesAndTransfers=" + releasesAndTransfers +
                '}';
    }
}
