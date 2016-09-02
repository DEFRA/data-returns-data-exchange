package uk.gov.ea.datareturns.domain.model;

import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.Parsed;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.io.csv.generic.AbstractCSVRecord;
import uk.gov.ea.datareturns.domain.model.rules.DataReturnsHeaders;
import uk.gov.ea.datareturns.domain.model.rules.conversion.EaIdConverter;
import uk.gov.ea.datareturns.domain.model.rules.conversion.ReturnsDateConverter;
import uk.gov.ea.datareturns.domain.model.rules.modifiers.field.*;
import uk.gov.ea.datareturns.domain.model.rules.modifiers.record.PostValidationModifier;
import uk.gov.ea.datareturns.domain.model.rules.modifiers.record.FinalValueModifier;
import uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist.*;
import uk.gov.ea.datareturns.domain.model.validation.auditors.dependencies.PrimaryFieldBlocksDependentAuditor;
import uk.gov.ea.datareturns.domain.model.validation.auditors.dependencies.PrimaryFieldRequiresDependentAuditor;
import uk.gov.ea.datareturns.domain.model.validation.auditors.dependencies.TxtValueSeeCommentRequiresCommentAuditor;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.model.validation.constraints.dependencies.DependentField;
import uk.gov.ea.datareturns.domain.model.validation.constraints.dependencies.RequireExactlyOneOf;
import uk.gov.ea.datareturns.domain.model.validation.constraints.field.ValidReturnsDate;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

/**
 * Represents an individual sample (data return) record entry
 *
 * @author Sam Gardner-Dell
 */
// One of Value or Txt_Value must be present but not both
@RequireExactlyOneOf(fieldGetters = {"getValue", "getTextValue"}, tooFewMessage = "{DR9999-Missing}", tooManyMessage = "{DR9999-Conflict}")

// If Value is present, we must have a Unit
@DependentField(primaryFieldGetter = "getValue", dependentFieldGetter = "getUnit", fieldName = DataReturnsHeaders.UNIT,
		auditor = PrimaryFieldRequiresDependentAuditor.class, message = "{DR9050-Missing}")

// If Txt_Value specified, unit must not be used
@DependentField(primaryFieldGetter = "getTextValue", dependentFieldGetter = "getUnit", fieldName = DataReturnsHeaders.UNIT,
		auditor = PrimaryFieldBlocksDependentAuditor.class, message = "{DR9050-Conflict}")

// If Txt_Value is "See Comment" then Comment must be present
@DependentField(primaryFieldGetter = "getTextValue", dependentFieldGetter = "getComments", fieldName = DataReturnsHeaders.COMMENTS,
		auditor = TxtValueSeeCommentRequiresCommentAuditor.class, message = "{DR9140-Missing}")

// Generic post process operator - used to map the Value OR Txt_Value to a resultant field
@PostValidationModifier(modifier = FinalValueModifier.class)
public class DataSample extends AbstractCSVRecord {
	/** Regular expression for fields which should only contain simple text (no special characters) */
	private static final String REGEX_SIMPLE_TEXT = "^[a-zA-Z0-9 ]*$";

	/** The EA Unique Identifier (EA_ID) */
	@Parsed(field = DataReturnsHeaders.EA_IDENTIFIER)
	@Convert(conversionClass = EaIdConverter.class)
	@Valid
	private EaId eaId;

	/** The site name (Site_Name) */
	@Parsed(field = DataReturnsHeaders.SITE_NAME)
	@Pattern(regexp = REGEX_SIMPLE_TEXT, message = "{DR9110-Incorrect}")
	@Length(max = 255, message = "{DR9110-Length}")
	@NotBlank(message = "{DR9110-Missing}")
	private String siteName;

	/** The return type (Rtn_Type) */
	@Parsed(field = DataReturnsHeaders.RETURN_TYPE)
	@NotBlank(message = "{DR9010-Missing}")
	@ControlledList(auditor = ReturnTypeAuditor.class, message = "{DR9010-Incorrect}")
	@Modifier(modifier = ReturnTypeModifier.class)
	private String returnType;

	/** The monitoring date (Mon_Date) */
	@Parsed(field = DataReturnsHeaders.MONITORING_DATE)
	@Convert(conversionClass = ReturnsDateConverter.class)
	@ValidReturnsDate // see ValidReturnsDate annotation class for validation messages
	private ReturnsDate monitoringDate;

	/** The return period  (Rtn_Period) */
	@Parsed(field = DataReturnsHeaders.RETURN_PERIOD)
	@ControlledList(auditor = ReturnPeriodAuditor.class, message = "{DR9070-Incorrect}")
	@Modifier(modifier = ReturnPeriodModifier.class)
	private String returnPeriod;

	/** The monitoring point (Mon_Point) */
	@Parsed(field = DataReturnsHeaders.MONITORING_POINT)
	@NotBlank(message = "{DR9060-Missing}")
	@Length(max = 30, message = "{DR9060-Length}")
	@Pattern(regexp = REGEX_SIMPLE_TEXT, message = "{DR9060-Incorrect}")
	private String monitoringPoint;

	/** Sample reference (Smpl_Ref) */
	@Parsed(field = DataReturnsHeaders.SAMPLE_REFERENCE)
	@Length(max = 255, message = "{DR9120-Length}")
	@Pattern(regexp = REGEX_SIMPLE_TEXT, message = "{DR9120-Incorrect}")
	private String sampleReference;

	/** Sampled by (Smpl_By) */
	@Parsed(field = DataReturnsHeaders.SAMPLE_BY)
	@Length(max = 255, message = "{DR9130-Length}")
	private String sampleBy;

	/** Parameter value (Parameter) */
	@Parsed(field = DataReturnsHeaders.PARAMETER)
	@NotBlank(message = "{DR9030-Missing}")
	@ControlledList(auditor = ParameterAuditor.class, message = "{DR9030-Incorrect}")
	@Modifier(modifier = ParameterModifier.class)
	private String parameter;

	/** Value (Value) */
	@Parsed(field = DataReturnsHeaders.VALUE)
	@Pattern(regexp = "([<>]?-?(\\d+\\.)?(\\d)+)", message = "{DR9040-Incorrect}")
	private String value;

	/** Textual value (Txt_Value) */
	@Parsed(field = DataReturnsHeaders.TEXT_VALUE)
	@ControlledList(auditor = TxtValueAuditor.class, message = "{DR9080-Incorrect}")
	@Modifier(modifier = TextValueModifier.class)
	private String textValue;

	/** Qualifier value (Qualifier) */
	@Parsed(field = DataReturnsHeaders.QUALIFIER)
	@ControlledList(auditor = QualifierAuditor.class, message = "{DR9180-Incorrect}")
	@Modifier(modifier = QualifierModifier.class)
	private String qualifiers;

	/** Unit of measurement (Unit) */
	@Parsed(field = DataReturnsHeaders.UNIT)
	@ControlledList(auditor = UnitAuditor.class, message = "{DR9050-Incorrect}")
	@Modifier(modifier = UnitModifier.class)
	private String unit;

	/** Reference period */
	@Parsed(field = DataReturnsHeaders.REFERENCE_PERIOD)
	@ControlledList(auditor = ReferencePeriodAuditor.class, message = "{DR9090-Incorrect}")
	@Modifier(modifier = ReferencePeriodModifier.class)
	private String referencePeriod;

	/** Method or standard used (Meth_Stand) */
	@Parsed(field = DataReturnsHeaders.METHOD_STANDARD)
	@ControlledList(auditor = MethodOrStandardAuditor.class, message = "{DR9100-Incorrect}")
	@Modifier(modifier = MethodOrStandardModifier.class)
	private String methStand;

	/** Record comments (Comments) */
	@Parsed(field = DataReturnsHeaders.COMMENTS)
	@Length(max = 255, message = "{DR9140-Length}")
	private String comments;

	/** Commercial in confidence data (CiC) */
	@Parsed(field = DataReturnsHeaders.COMMERCIAL_IN_CONFIDENCE)
	@Length(max = 255, message = "{DR9150-Length}")
	private String cic;

	/** Chemical Abstracts Service value (CAS) */
	@Parsed(field = DataReturnsHeaders.CHEMICAL_ABSTRACTS_SERVICE)
	@Length(max = 255, message = "{DR9160-Length}")
	private String cas;

	/** Recovery and disposal code (RD_Code) */
	@Parsed(field = DataReturnsHeaders.RECOVERY_AND_DISPOSAL_CODE)
	@Length(max = 255, message = "{DR9170-Length}")
	private String rdCode;

	/**
	 * Default constructor
	 */
	public DataSample() {
		super();
	}

	/**
	 * @return the eaId
	 */
	public EaId getEaId() {
		return this.eaId;
	}

	/**
	 #	 * @param eaId the eaId to set
	 */
	public void setEaId(final EaId eaId) {
		this.eaId = eaId;
	}

	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return this.siteName;
	}

	/**
	 * @param siteName the siteName to set
	 */
	public void setSiteName(final String siteName) {
		this.siteName = siteName;
	}

	/**
	 * @return the returnType
	 */
	public String getReturnType() {
		return this.returnType;
	}

	/**
	 * @param returnType the returnType to set
	 */
	public void setReturnType(final String returnType) {
		this.returnType = returnType;
	}

	/**
	 * @return the monitoringDate
	 */
	public ReturnsDate getMonitoringDate() {
		return this.monitoringDate;
	}

	/**
	 * @param monitoringDate the monitoringDate to set
	 */
	public void setMonitoringDate(final ReturnsDate monitoringDate) {
		this.monitoringDate = monitoringDate;
	}

	/**
	 * @return the returnPeriod
	 */
	public String getReturnPeriod() {
		return this.returnPeriod;
	}

	/**
	 * @param returnPeriod the returnPeriod to set
	 */
	public void setReturnPeriod(final String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	/**
	 * @return the monitoringPoint
	 */
	public String getMonitoringPoint() {
		return this.monitoringPoint;
	}

	/**
	 * @param monitoringPoint the monitoringPoint to set
	 */
	public void setMonitoringPoint(final String monitoringPoint) {
		this.monitoringPoint = monitoringPoint;
	}

	/**
	 * @return the sampleReference
	 */
	public String getSampleReference() {
		return this.sampleReference;
	}

	/**
	 * @param sampleReference the sampleReference to set
	 */
	public void setSampleReference(final String sampleReference) {
		this.sampleReference = sampleReference;
	}

	/**
	 * @return the sampleBy
	 */
	public String getSampleBy() {
		return this.sampleBy;
	}

	/**
	 * @param sampleBy the sampleBy to set
	 */
	public void setSampleBy(final String sampleBy) {
		this.sampleBy = sampleBy;
	}

	/**
	 * @return the parameter
	 */
	public String getParameter() {
		return this.parameter;
	}

	/**
	 * @param parameter the parameter to set
	 */
	public void setParameter(final String parameter) {
		this.parameter = parameter;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * @return the textValue
	 */
	public String getTextValue() {
		return this.textValue;
	}

	/**
	 * @param textValue the textValue to set
	 */
	public void setTextValue(final String textValue) {
		this.textValue = textValue;
	}

	/**
	 * @return the qualifier values
	 */
	public String getQualifiers() {
		return qualifiers;
	}

	/**
	 * @param qualifiers the qualifier values to set
	 */
	public void setQualifiers(String qualifiers) {
		this.qualifiers = qualifiers;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return this.unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(final String unit) {
		this.unit = unit;
	}

	/**
	 * @return the referencePeriod
	 */
	public String getReferencePeriod() {
		return this.referencePeriod;
	}

	/**
	 * @param referencePeriod the referencePeriod to set
	 */
	public void setReferencePeriod(final String referencePeriod) {
		this.referencePeriod = referencePeriod;
	}

	/**
	 * @return the methStand
	 */
	public String getMethStand() {
		return this.methStand;
	}

	/**
	 * @param methStand the methStand to set
	 */
	public void setMethStand(final String methStand) {
		this.methStand = methStand;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return this.comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(final String comments) {
		this.comments = comments;
	}

	/**
	 * @return the cic
	 */
	public String getCic() {
		return this.cic;
	}

	/**
	 * @param cic the cic to set
	 */
	public void setCic(final String cic) {
		this.cic = cic;
	}

	/**
	 * @return the cas
	 */
	public String getCas() {
		return this.cas;
	}

	/**
	 * @param cas the cas to set
	 */
	public void setCas(final String cas) {
		this.cas = cas;
	}

	/**
	 * @return the rdCode
	 */
	public String getRdCode() {
		return this.rdCode;
	}

	/**
	 * @param rdCode the rdCode to set
	 */
	public void setRdCode(final String rdCode) {
		this.rdCode = rdCode;
	}

}