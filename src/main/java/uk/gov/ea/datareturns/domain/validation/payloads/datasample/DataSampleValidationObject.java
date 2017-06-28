package uk.gov.ea.datareturns.domain.validation.payloads.datasample;

import uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.annotations.*;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.*;
import uk.gov.ea.datareturns.domain.validation.common.validator.AbstractValidationObject;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;

import javax.validation.Valid;

/**
 * @author Graham Willis
 * Object contaioning entityfields and hibernate validation annotations
 */

@ProhibitTxtValueWithValue
@RequireValueOrTxtValue
@ProhibitUnitWithTxtValue
@RequireUnitWithValue
@SiteMatchesUniqueIdentifier
@RequireCommentsForTextValueComment
public class DataSampleValidationObject extends AbstractValidationObject {

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
    @Valid private Value value;

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

    /** RecordEntity comments (Comments) */
    @Valid private Comments comments;

    /**
     * Initialize with a data transport object DTO
     *
     * @param payload
     */
    public DataSampleValidationObject(DataSamplePayload payload) {
        super(payload);

        eaId = new EaId(payload.getEaId());
        siteName = new SiteName(payload.getSiteName());
        returnType = new ReturnType(payload.getReturnType());
        returnPeriod = new ReturnPeriod(payload.getReturnPeriod());
        monitoringDate = new MonitoringDate(payload.getMonitoringDate());
        monitoringPoint = new MonitoringPoint(payload.getMonitoringPoint());
        parameter = new Parameter(payload.getParameter());
        unit = new Unit(payload.getUnit());
        value = new Value(payload.getValue());
        textValue = new TxtValue(payload.getTextValue());
        qualifier = new Qualifier(payload.getQualifier());
        referencePeriod = new ReferencePeriod(payload.getReferencePeriod());
        methStand = new MethodOrStandard(payload.getMethStand());
        comments = new Comments(payload.getComments());
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

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
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
        return "DataSampleValidationObject{" +
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
