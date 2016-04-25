package uk.gov.ea.datareturns.domain.model;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.Parsed;

import uk.gov.ea.datareturns.domain.io.csv.generic.AbstractCSVRecord;
import uk.gov.ea.datareturns.domain.model.rules.DataReturnsHeaders;
import uk.gov.ea.datareturns.domain.model.rules.conversion.ReturnsDateConverter;
import uk.gov.ea.datareturns.domain.model.validation.auditors.MethodOrStandardAuditor;
import uk.gov.ea.datareturns.domain.model.validation.auditors.MonitoringPeriodAuditor;
import uk.gov.ea.datareturns.domain.model.validation.auditors.ParameterAuditor;
import uk.gov.ea.datareturns.domain.model.validation.auditors.QualifierAuditor;
import uk.gov.ea.datareturns.domain.model.validation.auditors.ReferencePeriodAuditor;
import uk.gov.ea.datareturns.domain.model.validation.auditors.ReturnTypeAuditor;
import uk.gov.ea.datareturns.domain.model.validation.auditors.UniqueIdentifierAuditor;
import uk.gov.ea.datareturns.domain.model.validation.auditors.UnitAuditor;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.model.validation.constraints.field.ValidReturnsDate;

/**
 * Represents an individual monitoring data record entry
 *
 * @author Sam Gardner-Dell
 */
// TODO: Dependent field validation rules from new DEP
//@DependentField(primaryFieldGetter="getValue", 
//				dependentFieldGetter="getComments", 
//				auditor=CommentDefinedForNoValueAuditor.class,
//				message="{DR9999-Dependency}",
//				groups=DependentField.class)
public class MonitoringDataRecord extends AbstractCSVRecord {
	/** The Permit Number (EA_ID) */
	@Parsed(field=DataReturnsHeaders.PERMIT_NUMBER)
	@NotBlank(message = "{DR9000-Missing}")
	@Pattern(regexp = "(^[A-Za-z][A-Za-z].*|^[0-9]{5,6}$)", message = "{DR9000-Incorrect}")
	@ControlledList(auditor = UniqueIdentifierAuditor.class, message = "{DR9000-Incorrect}")
	private String permitNumber;

	/** The site name (Site_Name) */
	@Parsed(field=DataReturnsHeaders.SITE_NAME)
	@Pattern(regexp = "([0-9a-zA-Z ])*", message = "{DR9110-Incorrect}")
	@Length(min = 0, max = 255, message = "{DR9110-Length}")
	private String siteName;

	/** The return type (Rtn_Type) */
	@Parsed(field=DataReturnsHeaders.RETURN_TYPE)
	@NotBlank(message = "{DR9010-Missing}")
	@ControlledList(auditor = ReturnTypeAuditor.class, message = "{DR9010-Incorrect}")
	private String returnType;

	/** The monitoring date (Mon_Date) */
	@Parsed(field=DataReturnsHeaders.MONITORING_DATE)
	@Convert(conversionClass=ReturnsDateConverter.class)
	@ValidReturnsDate // see ValidReturnsDate annotation class for validation messages
	private ReturnsDate monitoringDate;

	/** The monitoring period  (Mon_Period) */
	@Parsed(field=DataReturnsHeaders.MONITORING_PERIOD)
	@ControlledList(auditor = MonitoringPeriodAuditor.class, message = "{DR9070-Incorrect}", required = false)
	private String monitoringPeriod;

	/** The monitoring point (Mon_Point) */
	@Parsed(field=DataReturnsHeaders.MONITORING_POINT)
	@NotBlank(message = "{DR9060-Missing}")
	@Length(min = 0, max = 30, message = "{DR9060-Length}")
	@Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "{DR9060-Incorrect}")
	private String monitoringPoint;

	/** Sample reference (Smpl_Ref) */
	@Parsed(field=DataReturnsHeaders.SAMPLE_REFERENCE)
	@Length(min = 0, max = 255, message = "{DR9120-Length}")
	@Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "{DR9120-Incorrect}")
	private String sampleReference;

	/** Sampled by (Smpl_By) */
	@Parsed(field=DataReturnsHeaders.SAMPLE_BY)
	@Length(min = 0, max = 255, message = "{DR9130-Length}")
	private String sampleBy;

	/** Parameter value (Parameter) */
	@Parsed(field=DataReturnsHeaders.PARAMETER)
	@NotBlank(message = "{DR9030-Missing}")
	@ControlledList(auditor = ParameterAuditor.class, message = "{DR9030-Incorrect}")
	private String parameter;

	/** Value (Value) */
	@Parsed(field=DataReturnsHeaders.VALUE)
	@NotBlank(message = "{DR9040-Missing}")
	@Pattern(regexp = "([<>]?\\-?(\\d+\\.)?(\\d)+)", message = "{DR9040-Incorrect}")
	private String value;

	/** Textual value (Txt_Value) */
	@Parsed(field=DataReturnsHeaders.TEXT_VALUE)
	@ControlledList(auditor = QualifierAuditor.class, message = "{DR9080-Incorrect}", required = false)
	private String textValue;

	/** Unit of measurement (Unit) */
	@Parsed(field=DataReturnsHeaders.UNIT)
	@NotBlank(message = "{DR9050-Missing}")
	@ControlledList(auditor = UnitAuditor.class, message = "{DR9050-Incorrect}")
	private String unit;

	/** Reference period */
	@Parsed(field=DataReturnsHeaders.REFERENCE_PERIOD)
	@ControlledList(auditor = ReferencePeriodAuditor.class, message = "{DR9090-Incorrect}", required = false)
	private String referencePeriod;

	/** Method or standard used (Meth_Stand) */
	@Parsed(field=DataReturnsHeaders.METHOD_STANDARD)
	@ControlledList(auditor = MethodOrStandardAuditor.class, message = "{DR9100-Incorrect}", required = false)
	private String methStand;

	/** Record comments (Comments) */
	@Parsed(field=DataReturnsHeaders.COMMENTS)
	@Length(min = 0, max = 255, message = "{DR9140-Length}")
	private String comments;

	/** Commercial in confidence data (CiC) */
	@Parsed(field=DataReturnsHeaders.COMMERCIAL_IN_CONFIDENCE)
	@Length(min = 0, max = 255, message = "{DR9150-Length}")
	private String cic;

	/** Chemical Abstracts Service value (CAS) */
	@Parsed(field=DataReturnsHeaders.CHEMICAL_ABSTRACTS_SERVICE)
	@Length(min = 0, max = 255, message = "{DR9160-Length}")
	private String cas;

	/** Recovery and disposal code (RD_Code) */
	@Parsed(field=DataReturnsHeaders.RECOVERY_AND_DISPOSAL_CODE)
	@Length(min = 0, max = 255, message = "{DR9170-Length}")
	private String rdCode;

	/**
	 * Default constructor
	 */
	public MonitoringDataRecord() {
		super();
	}

	/**
	 * @return the permitNumber
	 */
	public String getPermitNumber() {
		return permitNumber;
	}

	/**
	 * @param permitNumber the permitNumber to set
	 */
	public void setPermitNumber(String permitNumber) {
		this.permitNumber = permitNumber;
	}

	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return siteName;
	}

	/**
	 * @param siteName the siteName to set
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	/**
	 * @return the returnType
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * @param returnType the returnType to set
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	/**
	 * @return the monitoringDate
	 */
	public ReturnsDate getMonitoringDate() {
		return monitoringDate;
	}

	/**
	 * @param monitoringDate the monitoringDate to set
	 */
	public void setMonitoringDate(ReturnsDate monitoringDate) {
		this.monitoringDate = monitoringDate;
	}

	/**
	 * @return the monitoringPeriod
	 */
	public String getMonitoringPeriod() {
		return monitoringPeriod;
	}

	/**
	 * @param monitoringPeriod the monitoringPeriod to set
	 */
	public void setMonitoringPeriod(String monitoringPeriod) {
		this.monitoringPeriod = monitoringPeriod;
	}

	/**
	 * @return the monitoringPoint
	 */
	public String getMonitoringPoint() {
		return monitoringPoint;
	}

	/**
	 * @param monitoringPoint the monitoringPoint to set
	 */
	public void setMonitoringPoint(String monitoringPoint) {
		this.monitoringPoint = monitoringPoint;
	}

	/**
	 * @return the sampleReference
	 */
	public String getSampleReference() {
		return sampleReference;
	}

	/**
	 * @param sampleReference the sampleReference to set
	 */
	public void setSampleReference(String sampleReference) {
		this.sampleReference = sampleReference;
	}

	/**
	 * @return the sampleBy
	 */
	public String getSampleBy() {
		return sampleBy;
	}

	/**
	 * @param sampleBy the sampleBy to set
	 */
	public void setSampleBy(String sampleBy) {
		this.sampleBy = sampleBy;
	}

	/**
	 * @return the parameter
	 */
	public String getParameter() {
		return parameter;
	}

	/**
	 * @param parameter the parameter to set
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the textValue
	 */
	public String getTextValue() {
		return textValue;
	}

	/**
	 * @param textValue the textValue to set
	 */
	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * @return the referencePeriod
	 */
	public String getReferencePeriod() {
		return referencePeriod;
	}

	/**
	 * @param referencePeriod the referencePeriod to set
	 */
	public void setReferencePeriod(String referencePeriod) {
		this.referencePeriod = referencePeriod;
	}

	/**
	 * @return the methStand
	 */
	public String getMethStand() {
		return methStand;
	}

	/**
	 * @param methStand the methStand to set
	 */
	public void setMethStand(String methStand) {
		this.methStand = methStand;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the cic
	 */
	public String getCic() {
		return cic;
	}

	/**
	 * @param cic the cic to set
	 */
	public void setCic(String cic) {
		this.cic = cic;
	}

	/**
	 * @return the cas
	 */
	public String getCas() {
		return cas;
	}

	/**
	 * @param cas the cas to set
	 */
	public void setCas(String cas) {
		this.cas = cas;
	}

	/**
	 * @return the rdCode
	 */
	public String getRdCode() {
		return rdCode;
	}

	/**
	 * @param rdCode the rdCode to set
	 */
	public void setRdCode(String rdCode) {
		this.rdCode = rdCode;
	}
}
