package uk.gov.ea.datareturns.domain.model;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import uk.gov.ea.datareturns.domain.io.csv.generic.AbstractCSVRecord;
import uk.gov.ea.datareturns.domain.io.csv.generic.annotations.CSVField;
import uk.gov.ea.datareturns.domain.model.rules.DataReturnsHeaders;
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
@XmlAccessorType(XmlAccessType.FIELD)

// TODO: For a future release - finish setting up a mechanism for dependent
// field validation
// @DependentField(primaryFieldGetter="getReturnType",
public class MonitoringDataRecord extends AbstractCSVRecord {
	/** The Permit Number (EA_ID) */
	@CSVField(DataReturnsHeaders.PERMIT_NUMBER)
	@NotBlank(message = "{DR9000-Missing}")
	@Pattern(regexp = "(^[A-Za-z][A-Za-z].*|^[0-9]{5,6}$)", message = "{DR9000-Incorrect}")
	@ControlledList(auditor = UniqueIdentifierAuditor.class, message = "{DR9000-Incorrect}")
	private String permitNumber;

	/** The site name (Site_Name) */
	@CSVField(DataReturnsHeaders.SITE_NAME)
	@Pattern(regexp = "([0-9a-zA-Z ])*", message = "{DR9110-Incorrect}")
	@Length(min = 0, max = 255, message = "{DR9110-Length}")
	private String siteName;

	/** The return type (Rtn_Type) */
	@CSVField(DataReturnsHeaders.RETURN_TYPE)
	@NotBlank(message = "{DR9010-Missing}")
	@ControlledList(auditor = ReturnTypeAuditor.class, message = "{DR9010-Incorrect}")
	private String returnType;

	/** The monitoring date (Mon_Date) */
	@CSVField(DataReturnsHeaders.MONITORING_DATE)
	@ValidReturnsDate // see ValidReturnsDate annotation class for validation messages
	private String monitoringDate;

	/** The monitoring period  (Mon_Period) */
	@CSVField(DataReturnsHeaders.MONITORING_PERIOD)
	@ControlledList(auditor = MonitoringPeriodAuditor.class, message = "{DR9070-Incorrect}", required = false)
	private String monitoringPeriod;

	/** The monitoring point (Mon_Point) */
	@CSVField(DataReturnsHeaders.MONITORING_POINT)
	@NotBlank(message = "{DR9060-Missing}")
	@Length(min = 0, max = 30, message = "{DR9060-Length}")
	@Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "{DR9060-Incorrect}")
	private String monitoringPoint;

	/** Sample reference (Smpl_Ref) */
	@CSVField(DataReturnsHeaders.SAMPLE_REFERENCE)
	@Length(min = 0, max = 255, message = "{DR9120-Length}")
	@Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "{DR9120-Incorrect}")
	private String sampleReference;

	/** Sampled by (Smpl_By) */
	@CSVField(DataReturnsHeaders.SAMPLE_BY)
	@Length(min = 0, max = 255, message = "{DR9130-Length}")
	private String sampleBy;

	/** Parameter value (Parameter) */
	@CSVField(DataReturnsHeaders.PARAMETER)
	@NotBlank(message = "{DR9030-Missing}")
	@ControlledList(auditor = ParameterAuditor.class, message = "{DR9030-Incorrect}")
	private String parameter;

	/** Value (Value) */
	@CSVField(DataReturnsHeaders.VALUE)
	@NotBlank(message = "{DR9040-Missing}")
	@Pattern(regexp = "([<>]?\\-?(\\d+\\.)?(\\d)+)", message = "{DR9040-Incorrect}")
	private String value;

	/** Textual value (Txt_Value) */
	@CSVField(DataReturnsHeaders.TEXT_VALUE)
	@ControlledList(auditor = QualifierAuditor.class, message = "{DR9080-Incorrect}", required = false)
	private String textValue;

	/** Unit of measurement (Unit) */
	@CSVField(DataReturnsHeaders.UNIT)
	@NotBlank(message = "{DR9050-Missing}")
	@ControlledList(auditor = UnitAuditor.class, message = "{DR9050-Incorrect}")
	private String unit;

	/** Reference period */
	@CSVField(DataReturnsHeaders.REFERENCE_PERIOD)
	@ControlledList(auditor = ReferencePeriodAuditor.class, message = "{DR9090-Incorrect}", required = false)
	private String referencePeriod;

	/** Method or standard used (Meth_Stand) */
	@CSVField(DataReturnsHeaders.METHOD_STANDARD)
	@ControlledList(auditor = MethodOrStandardAuditor.class, message = "{DR9100-Incorrect}", required = false)
	private String methStand;

	/** Record comments (Comments) */
	@CSVField(DataReturnsHeaders.COMMENTS)
	@Length(min = 0, max = 255, message = "{DR9140-Length}")
	private String comments;

	/** Commercial in confidence data (CiC) */
	@CSVField(DataReturnsHeaders.COMMERCIAL_IN_CONFIDENCE)
	@Length(min = 0, max = 255, message = "{DR9150-Length}")
	private String cic;

	/** Chemical Abstracts Service value (CAS) */
	@CSVField(DataReturnsHeaders.CHEMICAL_ABSTRACTS_SERVICE)
	@Length(min = 0, max = 255, message = "{DR9160-Length}")
	private String cas;

	/** Recovery and disposal code (RD_Code) */
	@CSVField(DataReturnsHeaders.RECOVERY_AND_DISPOSAL_CODE)
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
		return this.permitNumber;
	}

	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return this.siteName;
	}

	/**
	 * @return the returnType
	 */
	public String getReturnType() {
		return this.returnType;
	}

	/**
	 * @return the monitoringDate
	 */
	public String getMonitoringDate() {
		return this.monitoringDate;
	}

	/**
	 * @return the monitoringPeriod
	 */
	public String getMonitoringPeriod() {
		return this.monitoringPeriod;
	}

	/**
	 * @return the monitoringPoint
	 */
	public String getMonitoringPoint() {
		return this.monitoringPoint;
	}

	/**
	 * @return the sampleReference
	 */
	public String getSampleReference() {
		return this.sampleReference;
	}

	/**
	 * @return the sampleBy
	 */
	public String getSampleBy() {
		return this.sampleBy;
	}

	/**
	 * @return the parameter
	 */
	public String getParameter() {
		return this.parameter;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @return the textValue
	 */
	public String getTextValue() {
		return this.textValue;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return this.unit;
	}

	/**
	 * @return the referencePeriod
	 */
	public String getReferencePeriod() {
		return this.referencePeriod;
	}

	/**
	 * @return the methStand
	 */
	public String getMethStand() {
		return this.methStand;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return this.comments;
	}

	/**
	 * @return the cic
	 */
	public String getCic() {
		return this.cic;
	}

	/**
	 * @return the cas
	 */
	public String getCas() {
		return this.cas;
	}

	/**
	 * @return the rdCode
	 */
	public String getRdCode() {
		return this.rdCode;
	}

	/**
	 * @param permitNumber
	 *            the permitNumber to set
	 */
	public void setPermitNumber(final String permitNumber) {
		this.permitNumber = permitNumber;
	}

	/**
	 * @param siteName
	 *            the siteName to set
	 */
	public void setSiteName(final String siteName) {
		this.siteName = siteName;
	}

	/**
	 * @param returnType
	 *            the returnType to set
	 */
	public void setReturnType(final String returnType) {
		this.returnType = returnType;
	}

	/**
	 * @param monitoringDate
	 *            the monitoringDate to set
	 */
	public void setMonitoringDate(final String monitoringDate) {
		this.monitoringDate = monitoringDate;
	}

	/**
	 * @param monitoringPeriod
	 *            the monitoringPeriod to set
	 */
	public void setMonitoringPeriod(final String monitoringPeriod) {
		this.monitoringPeriod = monitoringPeriod;
	}

	/**
	 * @param monitoringPoint
	 *            the monitoringPoint to set
	 */
	public void setMonitoringPoint(final String monitoringPoint) {
		this.monitoringPoint = monitoringPoint;
	}

	/**
	 * @param sampleReference
	 *            the sampleReference to set
	 */
	public void setSampleReference(final String sampleReference) {
		this.sampleReference = sampleReference;
	}

	/**
	 * @param sampleBy
	 *            the sampleBy to set
	 */
	public void setSampleBy(final String sampleBy) {
		this.sampleBy = sampleBy;
	}

	/**
	 * @param parameter
	 *            the parameter to set
	 */
	public void setParameter(final String parameter) {
		this.parameter = parameter;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * @param textValue
	 *            the textValue to set
	 */
	public void setTextValue(final String textValue) {
		this.textValue = textValue;
	}

	/**
	 * @param unit
	 *            the unit to set
	 */
	public void setUnit(final String unit) {
		this.unit = unit;
	}

	/**
	 * @param referencePeriod
	 *            the referencePeriod to set
	 */
	public void setReferencePeriod(final String referencePeriod) {
		this.referencePeriod = referencePeriod;
	}

	/**
	 * @param methStand
	 *            the methStand to set
	 */
	public void setMethStand(final String methStand) {
		this.methStand = methStand;
	}

	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(final String comments) {
		this.comments = comments;
	}

	/**
	 * @param cic
	 *            the cic to set
	 */
	public void setCic(final String cic) {
		this.cic = cic;
	}

	/**
	 * @param cas
	 *            the cas to set
	 */
	public void setCas(final String cas) {
		this.cas = cas;
	}

	/**
	 * @param rdCode
	 *            the rdCode to set
	 */
	public void setRdCode(final String rdCode) {
		this.rdCode = rdCode;
	}
}
