/**
 * 
 */
package uk.gov.ea.datareturns.domain.model.validation.auditors;

import java.util.HashSet;
import java.util.Set;

import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;

/**
 * Controlled list auditor for Return Types.
 * 
 * @author Sam Gardner-Dell
 */
public class ReturnTypeListAuditor implements ControlledListAuditor {
	private static final Set<String> RETURN_TYPES = new HashSet<>();
	static {
	      RETURN_TYPES.add("EPR/IED Air (point source) emissions return");
	      RETURN_TYPES.add("EPR/IED Air (ambient) monitoring return");
	      RETURN_TYPES.add("EPR/IED Leachate level return");
	      RETURN_TYPES.add("EPR/IED Leachate quality return");
	      RETURN_TYPES.add("EPR/IED Surface Water emissions return");
	      RETURN_TYPES.add("EPR/IED Groundwater quality/level return");
	      RETURN_TYPES.add("EPR/IED Landfill Gas surface emissions");
	      RETURN_TYPES.add("EPR/IED Landfill Gas Perimeter");
	      RETURN_TYPES.add("EPR/IED Landfill Gas in Waste");
	      RETURN_TYPES.add("EPR/IED Landfill Gas infrastructure monitoring");
	      RETURN_TYPES.add("EPR/IED Sewer emissions return");
	      RETURN_TYPES.add("EPR/IED ETP or Tankering emissions return");
	      RETURN_TYPES.add("EPR/IED Landfill Annual monitoring report");
	}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.model.validation.constraints.ControlledListAuditor#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(Object value) {
		return RETURN_TYPES.contains(value);
	}
}