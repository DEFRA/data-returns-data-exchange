package uk.gov.ea.datareturns.domain.validation.impl;

import uk.gov.ea.datareturns.domain.dto.impl.LandfillMeasurementDto;
import uk.gov.ea.datareturns.domain.model.fields.impl.*;
import uk.gov.ea.datareturns.domain.model.validation.constraints.factory.ValidRecord;
import uk.gov.ea.datareturns.domain.validation.Mvo;

import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * @author Graham Willis
 * Object contaioning fields and hibernate validation annotations
 */
@ValidRecord(value = LandfillMeasurementMvo.class)
public class LandfillMeasurementMvo extends Mvo<LandfillMeasurementDto> {

    /** The EA Unique Identifier (EA_ID) */
    @Valid private EaId eaId;

    /** The site name (Site_Name) */
    @Valid private SiteName siteName;

    /** The return type (Rtn_Type) */
    @Valid private ReturnType returnType;

    /** The monitoring date (Mon_Date) */
    @Valid private MonitoringDate monitoringDate;

    /** The return period  (Rtn_Period) */
    @Valid private ReturnPeriod returnPeriod;

    /** The monitoring point (Mon_Point) */
    @Valid private MonitoringPoint monitoringPoint;

    /** Parameter value (Parameter) */
    @Valid private Parameter parameter;

    /** Value (Value) */
    @Valid private BigDecimal value;

    /** Textual value (Txt_Value) */
    @Valid private TxtValue textValue;

    /** Qualifier value (Qualifier) */
    @Valid private Qualifier qualifier;

    /** Unit of measurement (Unit) */
    @Valid private Unit unit;

    /** Reference period */
    @Valid private ReferencePeriod referencePeriod;

    /** Method or standard used (Meth_Stand) */
    @Valid private MethodOrStandard methStand;

    /** Record comments (Comments) */
    @Valid private Comments comments;

    /**
     * Initialize with a data transport object DTO
     *
     * @param dto
     */
    public LandfillMeasurementMvo(LandfillMeasurementDto dto) {
        super(dto);

        // Initialize the fields for validation
        eaId = new EaId(dto.getEaId());
        siteName = new SiteName(dto.getSiteName());
        returnType = new ReturnType(dto.getReturnType());
        returnPeriod = new ReturnPeriod(dto.getReturnPeriod());
        monitoringDate = new MonitoringDate(dto.getMonitoringDate());
        monitoringPoint = new MonitoringPoint(dto.getMonitoringPoint());
        parameter = new Parameter(dto.getParameter());
        unit = new Unit(dto.getUnit());
        value = dto.getValue();
        textValue = new TxtValue(dto.getTextValue());
        qualifier = new Qualifier(dto.getQualifier());
        referencePeriod = new ReferencePeriod(dto.getReferencePeriod());
        methStand = new MethodOrStandard(dto.getMethStand());
        comments = new Comments(dto.getComments());
    }

    public EaId getEaId() {
        return eaId;
    }

    public void setEaId(EaId eaId) {
        this.eaId = eaId;
    }

    public SiteName getSiteName() {
        return siteName;
    }

    public void setSiteName(SiteName siteName) {
        this.siteName = siteName;
    }

    public ReturnType getReturnType() {
        return returnType;
    }

    public void setReturnType(ReturnType returnType) {
        this.returnType = returnType;
    }

    public MonitoringDate getMonitoringDate() {
        return monitoringDate;
    }

    public void setMonitoringDate(MonitoringDate monitoringDate) {
        this.monitoringDate = monitoringDate;
    }

    public ReturnPeriod getReturnPeriod() {
        return returnPeriod;
    }

    public void setReturnPeriod(ReturnPeriod returnPeriod) {
        this.returnPeriod = returnPeriod;
    }

    public MonitoringPoint getMonitoringPoint() {
        return monitoringPoint;
    }

    public void setMonitoringPoint(MonitoringPoint monitoringPoint) {
        this.monitoringPoint = monitoringPoint;
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

    public TxtValue getTextValue() {
        return textValue;
    }

    public void setTextValue(TxtValue textValue) {
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

    public MethodOrStandard getMethStand() {
        return methStand;
    }

    public void setMethStand(MethodOrStandard methStand) {
        this.methStand = methStand;
    }

    public Comments getComments() {
        return comments;
    }

    public void setComments(Comments comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "LandfillMeasurementMvo{" +
                "eaId=" + eaId +
                ", siteName=" + siteName +
                ", returnType=" + returnType +
                ", monitoringDate=" + monitoringDate +
                ", returnPeriod=" + returnPeriod +
                ", monitoringPoint=" + monitoringPoint +
                ", parameter=" + parameter +
                ", value=" + value +
                ", textValue=" + textValue +
                ", qualifier=" + qualifier +
                ", unit=" + unit +
                ", referencePeriod=" + referencePeriod +
                ", methStand=" + methStand +
                ", comments=" + comments +
                '}';
    }
}
