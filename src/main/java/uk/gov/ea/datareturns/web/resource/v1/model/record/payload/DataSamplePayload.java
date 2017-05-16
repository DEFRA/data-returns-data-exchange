package uk.gov.ea.datareturns.web.resource.v1.model.record.payload;

import io.swagger.annotations.ApiModel;

import javax.xml.bind.annotation.XmlElement;

/**
 * Payload containing DEP compliant monitoring data
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(value = "DataSamplePayload", description = "DEP v3.0 compliant monitoring data payload")
public class DataSamplePayload extends Payload {
    /** The EA Unique Identifier (EA_ID) */
    @XmlElement(name = "EA_ID")
    private String eaId;

    /** The site name (Site_Name) */
    @XmlElement(name = "Site_Name")
    private String siteName;

    /** The return type (Rtn_Type) */
    @XmlElement(name = "Rtn_Type")
    private String returnType;

    /** The monitoring date (Mon_Date) */
    @XmlElement(name = "Mon_Date")
    private String monitoringDate;

    /** The return period  (Rtn_Period) */
    @XmlElement(name = "Rtn_Period")
    private String returnPeriod;

    /** The monitoring point (Mon_Point) */
    @XmlElement(name = "Mon_Point")
    private String monitoringPoint;

    /** Parameter value (Parameter) */
    @XmlElement(name = "Parameter")
    private String parameter;

    /** Value (Value) */
    @XmlElement(name = "Value")
    private String value;

    /** Textual value (Txt_Value) */
    @XmlElement(name = "Txt_Value")
    private String textValue;

    /** Qualifier value (Qualifier) */
    @XmlElement(name = "Qualifier")
    private String qualifier;

    /** Unit of measurement (Unit) */
    @XmlElement(name = "Unit")
    private String unit;

    /** Reference period */
    @XmlElement(name = "Ref_Period")
    private String referencePeriod;

    /** Method or standard used (Meth_Stand) */
    @XmlElement(name = "Meth_Stand")
    private String methStand;

    /** Record comments (Comments) */
    @XmlElement(name = "Comments")
    private String comments;

    public String getEaId() {
        return eaId;
    }

    public void setEaId(String eaId) {
        this.eaId = eaId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getMonitoringDate() {
        return monitoringDate;
    }

    public void setMonitoringDate(String monitoringDate) {
        this.monitoringDate = monitoringDate;
    }

    public String getReturnPeriod() {
        return returnPeriod;
    }

    public void setReturnPeriod(String returnPeriod) {
        this.returnPeriod = returnPeriod;
    }

    public String getMonitoringPoint() {
        return monitoringPoint;
    }

    public void setMonitoringPoint(String monitoringPoint) {
        this.monitoringPoint = monitoringPoint;
    }

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

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getReferencePeriod() {
        return referencePeriod;
    }

    public void setReferencePeriod(String referencePeriod) {
        this.referencePeriod = referencePeriod;
    }

    public String getMethStand() {
        return methStand;
    }

    public void setMethStand(String methStand) {
        this.methStand = methStand;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
