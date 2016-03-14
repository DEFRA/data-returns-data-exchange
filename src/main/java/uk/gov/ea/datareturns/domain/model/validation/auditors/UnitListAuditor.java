/**
 * 
 */
package uk.gov.ea.datareturns.domain.model.validation.auditors;

import java.util.HashSet;
import java.util.Set;

import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;

/**
 * @author sam
 *
 */
public class UnitListAuditor implements ControlledListAuditor {
	private static final Set<String> UNITS = new HashSet<>();
	static {
        UNITS.add("ug");
        UNITS.add("mg");
        UNITS.add("g");
        UNITS.add("kg");
        UNITS.add("t");
        UNITS.add("mol");
        UNITS.add("mm");
        UNITS.add("cm");
        UNITS.add("m");
        UNITS.add("km");
        UNITS.add("mm2");
        UNITS.add("cm2");
        UNITS.add("m2");
        UNITS.add("km2");
        UNITS.add("Ha");
        UNITS.add("mm3");
        UNITS.add("cm3");
        UNITS.add("m3");
        UNITS.add("ul");
        UNITS.add("ml");
        UNITS.add("l");
        UNITS.add("s");
        UNITS.add("min");
        UNITS.add("hr");
        UNITS.add("day");
        UNITS.add("wk");
        UNITS.add("mth");
        UNITS.add("qrt");
        UNITS.add("yr");
        UNITS.add("J");
        UNITS.add("kJ");
        UNITS.add("MJ");
        UNITS.add("TJ");
        UNITS.add("W");
        UNITS.add("kW");
        UNITS.add("MW");
        UNITS.add("GW");
        UNITS.add("TW");
        UNITS.add("mV");
        UNITS.add("V");
        UNITS.add("kV");
        UNITS.add("MV");
        UNITS.add("mA");
        UNITS.add("A");
        UNITS.add("kA");
        UNITS.add("Hz");
        UNITS.add("kHz");
        UNITS.add("MHz");
        UNITS.add("Bq");
        UNITS.add("ºC");
        UNITS.add("DegC");
        UNITS.add("Pa");
        UNITS.add("mBar");
        UNITS.add("Bar");
        UNITS.add("pH");
        UNITS.add("%");
        UNITS.add("Hazen");
        UNITS.add("ppm");
        UNITS.add("ppb");
        UNITS.add("95%ile");
        UNITS.add("FTU");
        UNITS.add("ug/kg");
        UNITS.add("mg/kg");
        UNITS.add("g/t");
        UNITS.add("ug/l");
        UNITS.add("mg/l");
        UNITS.add("g/l");
        UNITS.add("kg/l");
        UNITS.add("ug/m3");
        UNITS.add("mg/m3");
        UNITS.add("g/m3");
        UNITS.add("kg/m3");
        UNITS.add("ug/m2");
        UNITS.add("mg/m2");
        UNITS.add("g/m2");
        UNITS.add("kg/m2");
        UNITS.add("% v/v");
        UNITS.add("ug/s");
        UNITS.add("mg/s");
        UNITS.add("g/s");
        UNITS.add("kg/s");
        UNITS.add("mg/min");
        UNITS.add("g/min");
        UNITS.add("kg/min");
        UNITS.add("t/min");
        UNITS.add("ug/hr");
        UNITS.add("mg/hr");
        UNITS.add("g/hr");
        UNITS.add("kg/hr");
        UNITS.add("t/hr");
        UNITS.add("ug/day");
        UNITS.add("mg/day");
        UNITS.add("g/day");
        UNITS.add("kg/day");
        UNITS.add("t/day");
        UNITS.add("ug/wk");
        UNITS.add("mg/wk");
        UNITS.add("g/wk");
        UNITS.add("kg/wk");
        UNITS.add("t/wk");
        UNITS.add("kg/yr");
        UNITS.add("t/yr");
        UNITS.add("mm3/s");
        UNITS.add("cm3/s");
        UNITS.add("m3/s");
        UNITS.add("ul/s");
        UNITS.add("ml/s");
        UNITS.add("l/s");
        UNITS.add("mm3/min");
        UNITS.add("cm3/min");
        UNITS.add("m3/min");
        UNITS.add("ul/min");
        UNITS.add("ml/min");
        UNITS.add("l/min");
        UNITS.add("cm3/hr");
        UNITS.add("m3/hr");
        UNITS.add("ul/hr");
        UNITS.add("ml/hr");
        UNITS.add("l/hr");
        UNITS.add("mm3/day");
        UNITS.add("cm3/day");
        UNITS.add("m3/day");
        UNITS.add("ul/day");
        UNITS.add("ml/day");
        UNITS.add("l/day");
        UNITS.add("mm3/wk");
        UNITS.add("cm3/wk");
        UNITS.add("m3/wk");
        UNITS.add("ul/wk");
        UNITS.add("ml/wk");
        UNITS.add("l/wk");
        UNITS.add("m3/yr");
        UNITS.add("l/yr");
        UNITS.add("cm/s");
        UNITS.add("m/s");
        UNITS.add("cm/min");
        UNITS.add("m/min");
        UNITS.add("cm/hr");
        UNITS.add("m/hr");
        UNITS.add("km/hr");
        UNITS.add("cm/day");
        UNITS.add("m/day");
        UNITS.add("km/day");
        UNITS.add("cm/wk");
        UNITS.add("m/wk");
        UNITS.add("km/wk");
        UNITS.add("cm/mth");
        UNITS.add("m/mth");
        UNITS.add("cm/yr");
        UNITS.add("m/yr");
        UNITS.add("km/yr");
        UNITS.add("Bq/cm2");
        UNITS.add("Bq/m2");
        UNITS.add("Bq/cm3");
        UNITS.add("Bq/m3");
        UNITS.add("Bq/g");
        UNITS.add("Bq/kg");
        UNITS.add("Bq/l");
        UNITS.add("kWh");
        UNITS.add("MWh");
        UNITS.add("GWh");
	}
	
	/**
	 * 
	 */
	public UnitListAuditor() {
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(Object value) {
		return UNITS.contains(value);
	}
}
