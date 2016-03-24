package uk.gov.ea.datareturns.domain.model;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import uk.gov.ea.datareturns.domain.io.csv.annotations.CSVField;
import uk.gov.ea.datareturns.domain.model.rules.DataReturnsHeaders;
import uk.gov.ea.datareturns.domain.model.validation.auditors.ParameterListAuditor;
import uk.gov.ea.datareturns.domain.model.validation.auditors.ReturnTypeListAuditor;
import uk.gov.ea.datareturns.domain.model.validation.auditors.UnitListAuditor;
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
public class MonitoringDataRecord {
	/** The Permit Number (EA_ID) */
	@CSVField(DataReturnsHeaders.PERMIT_NUMBER)
	@XmlElement(name = DataReturnsHeaders.PERMIT_NUMBER)
	@NotBlank(message = "{uk.gov.ea.datareturns.permitNumber.missing}")
	@Pattern(regexp = "(^[A-Za-z][A-Za-z].*|^[0-9]{5,6}$)", message = "{uk.gov.ea.datareturns.permitNumber.invalid}")
	private String permitNumber;

	/** The site name (Site_Name) */
	@CSVField(DataReturnsHeaders.SITE_NAME)
	@XmlElement(name = DataReturnsHeaders.SITE_NAME)
	@Pattern(regexp = "([0-9a-zA-Z ])*")
	@Length(min = 0, max = 255)
	private String siteName;

	/** The return type (Rtn_Type) */
	@CSVField(DataReturnsHeaders.RETURN_TYPE)
	@XmlElement(name = DataReturnsHeaders.RETURN_TYPE)
	@NotBlank(message = "{uk.gov.ea.datareturns.returnType.missing}")
	@ControlledList(auditor = ReturnTypeListAuditor.class, message = "{uk.gov.ea.datareturns.returnType.invalid}")
	private String returnType;

	/** The monitoring date (Mon_Date) */
	@CSVField(DataReturnsHeaders.MONITORING_DATE)
	@XmlElement(name = DataReturnsHeaders.MONITORING_DATE)
	@ValidReturnsDate
	private String monitoringDate;

	/** The monitoring period  (Mon_Period) */
	@CSVField(DataReturnsHeaders.MONITORING_PERIOD)
	@XmlElement(name = DataReturnsHeaders.MONITORING_PERIOD)
	@Length(min = 0, max = 30, message = "{uk.gov.ea.datareturns.monitoringPeriod.length}")
	private String monitoringPeriod;

	/** The monitoring point (Mon_Point) */
	@CSVField(DataReturnsHeaders.MONITORING_POINT)
	@XmlElement(name = DataReturnsHeaders.MONITORING_POINT)
	@NotBlank(message = "{uk.gov.ea.datareturns.monitoringPoint.missing}")
	@Length(min = 0, max = 30, message = "{uk.gov.ea.datareturns.monitoringPoint.length}")
	@Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "{uk.gov.ea.datareturns.monitoringPoint.invalid}")
	private String monitoringPoint;

	/** Sample reference (Smpl_Ref) */
	@CSVField(DataReturnsHeaders.SAMPLE_REFERENCE)
	@XmlElement(name = DataReturnsHeaders.SAMPLE_REFERENCE)
	@Length(min = 0, max = 255, message = "{uk.gov.ea.datareturns.sampleReference.length}")
	@Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "{uk.gov.ea.datareturns.sampleReference.invalid}")
	private String sampleReference;

	/** Sampled by (Smpl_By) */
	@CSVField(DataReturnsHeaders.SAMPLE_BY)
	@XmlElement(name = DataReturnsHeaders.SAMPLE_BY)
	@Length(min = 0, max = 255, message = "{uk.gov.ea.datareturns.sampleBy.length}")
	private String sampleBy;

	/** Parameter value (Parameter) */
	@CSVField(DataReturnsHeaders.PARAMETER)
	@XmlElement(name = DataReturnsHeaders.PARAMETER)
	@ControlledList(auditor=ParameterListAuditor.class, message = "{uk.gov.ea.datareturns.parameter.invalid}")
	private String parameter;

	/** Value (Value) */
	@CSVField(DataReturnsHeaders.VALUE)
	@XmlElement(name = DataReturnsHeaders.VALUE)
	@NotBlank(message = "{uk.gov.ea.datareturns.value.missing}")
	@Pattern(regexp = "([<>]?\\-?(\\d+\\.)?(\\d)+)", message = "{uk.gov.ea.datareturns.value.invalid}")
	private String value;

	/** Textual value (Txt_Value) */
	@CSVField(DataReturnsHeaders.TEXT_VALUE)
	@XmlElement(name = DataReturnsHeaders.TEXT_VALUE)
	@Length(min = 0, max = 255, message = "{uk.gov.ea.datareturns.textValue.length}")
	private String textValue;

	/** Unit of measurement (Unit) */
	@CSVField(DataReturnsHeaders.UNIT)
	@XmlElement(name = DataReturnsHeaders.UNIT)
	@ControlledList(auditor=UnitListAuditor.class, message = "{uk.gov.ea.datareturns.unit.invalid}")
	private String unit;

	/** Reference period */
	@CSVField(DataReturnsHeaders.REFERENCE_PERIOD)
	@XmlElement(name = DataReturnsHeaders.REFERENCE_PERIOD)
	@Length(min = 0, max = 255, message = "{uk.gov.ea.datareturns.referencePeriod.length}")
	private String referencePeriod;

	/** Method or standard used (Meth_Stand) */
	@CSVField(DataReturnsHeaders.METHOD_STANDARD)
	@XmlElement(name = DataReturnsHeaders.METHOD_STANDARD)
	@Length(min = 0, max = 30, message = "{uk.gov.ea.datareturns.methStand.length}")
	private String methStand;

	/** Record comments (Comments) */
	@CSVField(DataReturnsHeaders.COMMENTS)
	@XmlElement(name = DataReturnsHeaders.COMMENTS)
	@Length(min = 0, max = 255, message = "{uk.gov.ea.datareturns.comments.length}")
	private String comments;

	/** Commercial in confidence data (CiC) */
	@CSVField(DataReturnsHeaders.COMMERCIAL_IN_CONFIDENCE)
	@XmlElement(name = DataReturnsHeaders.COMMERCIAL_IN_CONFIDENCE)
	@Length(min = 0, max = 255, message = "{uk.gov.ea.datareturns.cic.length}")
	private String cic;

	/** Chemical Abstracts Service value (CAS) */
	@CSVField(DataReturnsHeaders.CHEMICAL_ABSTRACTS_SERVICE)
	@XmlElement(name = DataReturnsHeaders.CHEMICAL_ABSTRACTS_SERVICE)
	@Length(min = 0, max = 255, message = "{uk.gov.ea.datareturns.cas.length}")
	private String cas;

	/** Recovery and disposal code (RD_Code) */
	@CSVField(DataReturnsHeaders.RECOVERY_AND_DISPOSAL_CODE)
	@XmlElement(name = DataReturnsHeaders.RECOVERY_AND_DISPOSAL_CODE)
	@Length(min = 0, max = 255, message = "{uk.gov.ea.datareturns.rdCode.length}")
	private String rdCode;

	/**
	 * Default constructor
	 */
	public MonitoringDataRecord() {
	}

	/**
	 * @return the permitNumber
	 */
	public String getPermitNumber() {
		return permitNumber;
	}

	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return siteName;
	}

	/**
	 * @return the returnType
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * @return the monitoringDate
	 */
	public String getMonitoringDate() {
		return monitoringDate;
	}

	/**
	 * @return the monitoringPeriod
	 */
	public String getMonitoringPeriod() {
		return monitoringPeriod;
	}

	/**
	 * @return the monitoringPoint
	 */
	public String getMonitoringPoint() {
		return monitoringPoint;
	}

	/**
	 * @return the sampleReference
	 */
	public String getSampleReference() {
		return sampleReference;
	}

	/**
	 * @return the sampleBy
	 */
	public String getSampleBy() {
		return sampleBy;
	}

	/**
	 * @return the parameter
	 */
	public String getParameter() {
		return parameter;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return the textValue
	 */
	public String getTextValue() {
		return textValue;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @return the referencePeriod
	 */
	public String getReferencePeriod() {
		return referencePeriod;
	}

	/**
	 * @return the methStand
	 */
	public String getMethStand() {
		return methStand;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @return the cic
	 */
	public String getCic() {
		return cic;
	}

	/**
	 * @return the cas
	 */
	public String getCas() {
		return cas;
	}

	/**
	 * @return the rdCode
	 */
	public String getRdCode() {
		return rdCode;
	}

	/**
	 * @param permitNumber
	 *            the permitNumber to set
	 */
	public void setPermitNumber(String permitNumber) {
		this.permitNumber = permitNumber;
	}

	/**
	 * @param siteName
	 *            the siteName to set
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	/**
	 * @param returnType
	 *            the returnType to set
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	/**
	 * @param monitoringDate
	 *            the monitoringDate to set
	 */
	public void setMonitoringDate(String monitoringDate) {
		this.monitoringDate = monitoringDate;
	}

	/**
	 * @param monitoringPeriod
	 *            the monitoringPeriod to set
	 */
	public void setMonitoringPeriod(String monitoringPeriod) {
		this.monitoringPeriod = monitoringPeriod;
	}

	/**
	 * @param monitoringPoint
	 *            the monitoringPoint to set
	 */
	public void setMonitoringPoint(String monitoringPoint) {
		this.monitoringPoint = monitoringPoint;
	}

	/**
	 * @param sampleReference
	 *            the sampleReference to set
	 */
	public void setSampleReference(String sampleReference) {
		this.sampleReference = sampleReference;
	}

	/**
	 * @param sampleBy
	 *            the sampleBy to set
	 */
	public void setSampleBy(String sampleBy) {
		this.sampleBy = sampleBy;
	}

	/**
	 * @param parameter
	 *            the parameter to set
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @param textValue
	 *            the textValue to set
	 */
	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}

	/**
	 * @param unit
	 *            the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * @param referencePeriod
	 *            the referencePeriod to set
	 */
	public void setReferencePeriod(String referencePeriod) {
		this.referencePeriod = referencePeriod;
	}

	/**
	 * @param methStand
	 *            the methStand to set
	 */
	public void setMethStand(String methStand) {
		this.methStand = methStand;
	}

	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @param cic
	 *            the cic to set
	 */
	public void setCic(String cic) {
		this.cic = cic;
	}

	/**
	 * @param cas
	 *            the cas to set
	 */
	public void setCas(String cas) {
		this.cas = cas;
	}

	/**
	 * @param rdCode
	 *            the rdCode to set
	 */
	public void setRdCode(String rdCode) {
		this.rdCode = rdCode;
	}
}
