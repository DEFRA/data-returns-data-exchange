/**
 *
 */
package uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Controlled list auditor for return periods
 *
 * @author Sam Gardner-Dell
 */
@Component
public class ReturnPeriodAuditor implements ControlledListAuditor {
	private static final String REGEX_YEAR = "(?<year>\\d{4})";
	private static final String REGEX_WEEK = "Week (?<week>\\d{1,2})";
	private static final String REGEX_MONTHS = "(?<month>Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) " + REGEX_YEAR;
	private static final String REGEX_QUARTERS = "(?<qtr>Qtr [1234]) " + REGEX_YEAR;
	private static final String REGEX_FINANCIAL_YEAR = "(?<start>\\d{2}|\\d{4})/(?<end>\\d{2})";
	private static final String REGEX_WATER_YEAR = "Water year " + REGEX_YEAR;

	private static final Pattern PTN_YEAR = Pattern.compile(REGEX_YEAR);
	private static final Pattern PTN_WEEK = Pattern.compile(REGEX_WEEK);
	private static final Pattern PTN_MONTHS = Pattern.compile(REGEX_MONTHS);
	private static final Pattern PTN_QUARTERS = Pattern.compile(REGEX_QUARTERS);
	private static final Pattern PTN_FINANCIAL_YEAR = Pattern.compile(REGEX_FINANCIAL_YEAR);
	private static final Pattern PTN_WATER_YEAR = Pattern.compile(REGEX_WATER_YEAR);

	/**
	 * All valid expressions - done like this for MVP, we will extend the validation in a future sprint to check the year/week values
	 * provided by the operator (which is why I have split all this up already)
	 */
	private static final Pattern[] ALL_PATTERNS = {PTN_YEAR, PTN_WEEK, PTN_MONTHS, PTN_QUARTERS, PTN_FINANCIAL_YEAR, PTN_WATER_YEAR};

	/**
	 *
	 */
	public ReturnPeriodAuditor() {}

	/* (non-Javadoc)
	 * @see uk.gov.ea.datareturns.domain.model.validation.constraints.ControlledListAuditor#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(final Object value) {
		for (Pattern p : ALL_PATTERNS) {
			if (p.matcher(Objects.toString(value, "")).matches()) {
				return true;
			}
		}
		return false;
	}
}