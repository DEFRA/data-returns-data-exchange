/**
 *
 */
package uk.gov.ea.datareturns.domain.model.rules;

/**
 * TODO: Move this stuff into the database
 *
 * @author Sam Gardner-Dell
 */
public enum FieldDefinition {
	/** The EA Unique Identifier (EA_ID) */
	EA_ID("Environment Agency unique identifier", "http://data-returns-help.herokuapp.com/help/detailed-guides/regime-specific-rules"),
	/** The site name (Site_Name) */
	Site_Name(null, "http://data-returns-help.herokuapp.com/help/detailed-guides/regime-specific-rules"),
	/** The return type (Rtn_Type) */
	Rtn_Type("Return type", "http://data-returns-help.herokuapp.com/help/detailed-guides/return-type"),
	/** The monitoring date (Mon_Date) */
	Mon_Date("Monitoring date", "http://data-returns-help.herokuapp.com/help/detailed-guides/regime-specific-rules"),
	/** The return period (Rtn_Period) */
	Rtn_Period("Return period", "http://data-returns-help.herokuapp.com/help/detailed-guides/monitoring-period"),
	/** The monitoring point (Mon_Point) */
	Mon_Point("Monitor point reference", "http://data-returns-help.herokuapp.com/help/detailed-guides/regime-specific-rules"),
	/** Sample reference (Smpl_Ref) */
	Smpl_Ref("Sample reference", "http://data-returns-help.herokuapp.com/help/detailed-guides/regime-specific-rules"),
	/** Sampled by (Smpl_By) */
	Smpl_By("Sampled by", "http://data-returns-help.herokuapp.com/help/detailed-guides/regime-specific-rules"),
	/** Parameter value (Parameter) */
	Parameter("Parameter", "http://data-returns-help.herokuapp.com/help/detailed-guides/parameters"),
	/** Value (Value) */
	Value("Value", "http://data-returns-help.herokuapp.com/help/detailed-guides/regime-specific-rules"),
	/** Textual value (Txt_Value) */
	Txt_Value("Text value", "http://data-returns-help.herokuapp.com/help/detailed-guides/qualifiers"),
	/** Unit of measurement (Unit) */
	Unit(null, "http://data-returns-help.herokuapp.com/help/detailed-guides/units-and-measures"),
	/** Reference period */
	Ref_Period("Reference period", "http://data-returns-help.herokuapp.com/help/detailed-guides/reference-periods"),
	/** Method or standard used (Meth_Stand) */
	Meth_Stand("Monitoring method or standard", "http://data-returns-help.herokuapp.com/help/detailed-guides/monitoring-standard"),
	/** Record comments (Comments) */
	Comments(null, "http://data-returns-help.herokuapp.com/help/detailed-guides/regime-specific-rules"),
	/** Commercial in confidence data (CiC) */
	CiC("Commercial in confidence", null),
	/** Chemical Abstracts Service value (CAS) */
	CAS("Chemical Abstracts Service number", null),
	/** Recovery and disposal code (RD_Code) */
	RD_Code("Recovery or disposal code", null),
	/** Qualifier (Qualifier) */
	Qualifier("Qualifier", "display-list?list=qualifier");

	private final String description;

	private final String helpReference;

	/**
	 * Create a new FieldDefinition
	 *
	 * @param description the long description for the field (if required)
	 * @param helpReference the reference to the help page for the field definition
	 */
	FieldDefinition(final String description, final String helpReference) {
		this.description = description;
		this.helpReference = helpReference;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return the helpReference
	 */
	public String getHelpReference() {
		return this.helpReference;
	}
}
