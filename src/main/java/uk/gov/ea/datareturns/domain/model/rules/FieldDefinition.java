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
	EA_ID("Environment Agency unique identifier", null),
	Site_Name(null, null),
	Rtn_Type("Return type", "http://data-returns-help.herokuapp.com/help/detailed-guides/return-type"),
	Mon_Date("Monitoring date", "http://data-returns-help.herokuapp.com/help/detailed-guides/date-and-time"),
	Mon_Period("Monitoring period", "http://data-returns-help.herokuapp.com/help/detailed-guides/monitoring-period"),
	Mon_Point("Monitor point reference", null),
	Smpl_Ref("Sample reference", null),
	Smpl_By("Sampled by", null),
	Parameter("Parameter", "http://data-returns-help.herokuapp.com/help/detailed-guides/parameters"),
	Value("Value", null),
	Txt_Value("Text value", "http://http://data-returns-help.herokuapp.com/help/detailed-guides/qualifiers"),
	Unit(null, "http://data-returns-help.herokuapp.com/help/detailed-guides/units-and-measures"),
	Ref_Period("Reference period", "http://data-returns-help.herokuapp.com/help/detailed-guides/reference-periods"),
	Meth_Stand("Monitoring method or standard", "http://data-returns-help.herokuapp.com/help/detailed-guides/monitoring-standard"),
	Comments(null, null),
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
