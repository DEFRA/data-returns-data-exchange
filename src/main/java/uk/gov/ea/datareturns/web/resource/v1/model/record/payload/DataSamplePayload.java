package uk.gov.ea.datareturns.web.resource.v1.model.record.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

/**
 * Payload containing DEP compliant monitoring data
 *
 * @author Sam Gardner-Dell
 */
@ApiModel(value = "DataSamplePayload", description = "DEP v3.0 compliant monitoring data payload", parent = Payload.class)
public class DataSamplePayload extends Payload  {

    public DataSamplePayload() {}

    public DataSamplePayload(final DataSamplePayload dataSamplePayload) {
        this.eaId = dataSamplePayload.eaId;
        this.siteName = dataSamplePayload.siteName;
        this.returnType = dataSamplePayload.returnType;
        this.monitoringDate = dataSamplePayload.monitoringDate;
        this.returnPeriod = dataSamplePayload.returnPeriod;
        this.monitoringPoint = dataSamplePayload.monitoringPoint;
        this.parameter = dataSamplePayload.parameter;
        this.value = dataSamplePayload.value;
        this.textValue = dataSamplePayload.textValue;
        this.qualifier = dataSamplePayload.qualifier;
        this.unit = dataSamplePayload.unit;
        this.referencePeriod = dataSamplePayload.referencePeriod;
        this.methStand = dataSamplePayload.methStand;
        this.comments = dataSamplePayload.comments;
    }

    /** The EA Unique Identifier (EA_ID) */
    @JsonProperty("EA_ID")
    private String eaId;

    /** The site name (Site_Name) */
    @JsonProperty("Site_Name")
    private String siteName;

    /** The return type (Rtn_Type) */
    @JsonProperty("Rtn_Type")
    private String returnType;

    /** The monitoring date (Mon_Date) */
    @JsonProperty("Mon_Date")
    private String monitoringDate;

    /** The return period  (Rtn_Period) */
    @JsonProperty("Rtn_Period")
    private String returnPeriod;

    /** The monitoring point (Mon_Point) */
    @JsonProperty("Mon_Point")
    private String monitoringPoint;

    /** Parameter value (Parameter) */
    @JsonProperty("Parameter")
    private String parameter;

    /** Value (Value) */
    @JsonProperty("Value")
    private String value;

    /** Textual value (Txt_Value) */
    @JsonProperty("Txt_Value")
    private String textValue;

    /** Qualifier value (Qualifier) */
    @JsonProperty("Qualifier")
    private String qualifier;

    /** Unit of measurement (Unit) */
    @JsonProperty("Unit")
    private String unit;

    /** Reference period */
    @JsonProperty("Ref_Period")
    private String referencePeriod;

    /** Method or standard used (Meth_Stand) */
    @JsonProperty("Meth_Stand")
    private String methStand;

    /** RecordEntity comments (Comments) */
    @JsonProperty("Comments")
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
