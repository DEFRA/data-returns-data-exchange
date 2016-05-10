package uk.gov.ea.datareturns.domain.model.rules;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Defines the set of header fields used by the Data Returns project
 *
 * @author Sam Gardner-Dell
 */
public class DataReturnsHeaders {
	/** The EA Unique Identifier (EA_ID) */
	public static final String EA_IDENTIFIER = "EA_ID";

	/** The site name (Site_Name) */
	public static final String SITE_NAME = "Site_Name";

	/** The return type (Rtn_Type) */
	public static final String RETURN_TYPE = "Rtn_Type";

	/** The monitoring date (Mon_Date) */
	public static final String MONITORING_DATE = "Mon_Date";

	/** The monitoring period (Mon_Period) */
	public static final String MONITORING_PERIOD = "Mon_Period";

	/** The monitoring point (Mon_Point) */
	public static final String MONITORING_POINT = "Mon_Point";

	/** Sample reference (Smpl_Ref) */
	public static final String SAMPLE_REFERENCE = "Smpl_Ref";

	/** Sampled by (Smpl_By) */
	public static final String SAMPLE_BY = "Smpl_By";

	/** Parameter value (Parameter) */
	public static final String PARAMETER = "Parameter";

	/** Value (Value) */
	public static final String VALUE = "Value";

	/** Textual value (Txt_Value) */
	public static final String TEXT_VALUE = "Txt_Value";

	/** Unit of measurement (Unit) */
	public static final String UNIT = "Unit";

	/** Reference period */
	public static final String REFERENCE_PERIOD = "Ref_Period";

	/** Method or standard used (Meth_Stand) */
	public static final String METHOD_STANDARD = "Meth_Stand";

	/** Record comments (Comments) */
	public static final String COMMENTS = "Comments";

	/** Commercial in confidence data (CiC) */
	public static final String COMMERCIAL_IN_CONFIDENCE = "CiC";

	/** Chemical Abstracts Service value (CAS) */
	public static final String CHEMICAL_ABSTRACTS_SERVICE = "CAS";

	/** Recovery and disposal code (RD_Code) */
	public static final String RECOVERY_AND_DISPOSAL_CODE = "RD_Code";

	/**
	 * The set of all headings allowed in the input data
	 */
	private static final Set<String> ALL_VALID_HEADINGS = new LinkedHashSet<>();
	static {
		ALL_VALID_HEADINGS.add(EA_IDENTIFIER);
		ALL_VALID_HEADINGS.add(SITE_NAME);
		ALL_VALID_HEADINGS.add(RETURN_TYPE);
		ALL_VALID_HEADINGS.add(MONITORING_DATE);
		ALL_VALID_HEADINGS.add(MONITORING_PERIOD);
		ALL_VALID_HEADINGS.add(MONITORING_POINT);
		ALL_VALID_HEADINGS.add(SAMPLE_REFERENCE);
		ALL_VALID_HEADINGS.add(SAMPLE_BY);
		ALL_VALID_HEADINGS.add(PARAMETER);
		ALL_VALID_HEADINGS.add(VALUE);
		ALL_VALID_HEADINGS.add(TEXT_VALUE);
		ALL_VALID_HEADINGS.add(UNIT);
		ALL_VALID_HEADINGS.add(REFERENCE_PERIOD);
		ALL_VALID_HEADINGS.add(METHOD_STANDARD);
		ALL_VALID_HEADINGS.add(COMMENTS);
		ALL_VALID_HEADINGS.add(COMMERCIAL_IN_CONFIDENCE);
		ALL_VALID_HEADINGS.add(CHEMICAL_ABSTRACTS_SERVICE);
		ALL_VALID_HEADINGS.add(RECOVERY_AND_DISPOSAL_CODE);
	}
	/** Store an array of all headings */
	private static final String[] ALL_HEADINGS_ARR = ALL_VALID_HEADINGS.toArray(new String[ALL_VALID_HEADINGS.size()]);
	
	/**
	 * The set of headings that MUST be defined in the input data
	 */
	private static final Set<String> MANDATORY_HEADINGS = new LinkedHashSet<>();
	static {
		MANDATORY_HEADINGS.add(EA_IDENTIFIER);
		MANDATORY_HEADINGS.add(RETURN_TYPE);
		MANDATORY_HEADINGS.add(MONITORING_DATE);
		MANDATORY_HEADINGS.add(MONITORING_POINT);
		MANDATORY_HEADINGS.add(PARAMETER);
		MANDATORY_HEADINGS.add(VALUE);
		MANDATORY_HEADINGS.add(UNIT);
	}

	/**
	 * @return the set of all headings allowed in the input data
	 */
	public static final Set<String> getAllHeadings() {
		return Collections.unmodifiableSet(ALL_VALID_HEADINGS);
	}

	/**
	 * @return the set of all headings allowed in the input data
	 */
	public static final String[] getAllHeadingsArray() {
		return ALL_HEADINGS_ARR;
	}

	/**
	 * @return the set of headings that MUST be defined in the input data
	 */
	public static final Set<String> getMandatoryHeadings() {
		return Collections.unmodifiableSet(MANDATORY_HEADINGS);
	}
}
