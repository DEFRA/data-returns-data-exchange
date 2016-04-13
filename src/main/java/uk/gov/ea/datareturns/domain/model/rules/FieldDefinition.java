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
	EA_ID("Environment Agency unique identifier", "http://data-returns-help.herokuapp.com/help/detailed-guides/regime-specific-rules"),
	Site_Name(null, "http://data-returns-help.herokuapp.com/help/detailed-guides/regime-specific-rules"),
	Rtn_Type("Return type", "http://data-returns-help.herokuapp.com/help/detailed-guides/return-type"),
	Mon_Date("Monitoring date", "http://data-returns-help.herokuapp.com/help/detailed-guides/regime-specific-rules"),
	Mon_Period("Monitoring period", "http://data-returns-help.herokuapp.com/help/detailed-guides/monitoring-period"),
	Mon_Point("Monitor point reference", "http://data-returns-help.herokuapp.com/help/detailed-guides/regime-specific-rules"),
	Smpl_Ref("Sample reference", "http://data-returns-help.herokuapp.com/help/detailed-guides/regime-specific-rules"),
	Smpl_By("Sampled by", "http://data-returns-help.herokuapp.com/help/detailed-guides/regime-specific-rules"),
	Parameter("Parameter", "http://data-returns-help.herokuapp.com/help/detailed-guides/parameters"),
	Value("Value", "http://data-returns-help.herokuapp.com/help/detailed-guides/regime-specific-rules"),
	Txt_Value("Text value", "http://data-returns-help.herokuapp.com/help/detailed-guides/qualifiers"),
	Unit(null, "http://data-returns-help.herokuapp.com/help/detailed-guides/units-and-measures"),
	Ref_Period("Reference period", "http://data-returns-help.herokuapp.com/help/detailed-guides/reference-periods"),
	Meth_Stand("Monitoring method or standard", "http://data-returns-help.herokuapp.com/help/detailed-guides/monitoring-standard"),
	Comments(null, "http://data-returns-help.herokuapp.com/help/detailed-guides/regime-specific-rules"),
	CiC("Commercial in confidence", null),
	CAS("Chemical Abstracts Service number", null),
	RD_Code("Recovery or disposal code", null);
	
	private final String description;
	private final String helpReference;
	
	private FieldDefinition(String description, String helpReference) {
		this.description = description;
		this.helpReference = helpReference;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the helpReference
	 */
	public String getHelpReference() {
		return helpReference;
	}
}
