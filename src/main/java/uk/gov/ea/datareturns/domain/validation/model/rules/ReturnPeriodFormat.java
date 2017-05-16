package uk.gov.ea.datareturns.domain.validation.model.rules;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Define the available formats for the Rtn_Period field and provide substitution functionality
 *
 * @author Sam Gardner-Dell
 */
public enum ReturnPeriodFormat {
    YEAR("\\s*(?<year>\\d{4})\\s*", "${year}"),
    WEEK("\\s*Week\\s*(?<week>\\d{1,2})\\s+(?<year>\\d{4})\\s*", "Week ${week} ${year}"),
    MONTH("\\s*(?<month>Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s*(?<year>\\d{4})\\s*", "${month} ${year}"),
    QUARTER("\\s*Qtr\\s*(?<qtr>[1234])\\s*(?<year>\\d{4})\\s*", "Qtr ${qtr} ${year}"),
    FINANCIAL_YEAR("\\s*(?<start>\\d{2}|\\d{4})\\s*/\\s*(?<end>\\d{2})\\s*", "${start}/${end}"),
    WATER_YEAR("\\s*Water\\s*year\\s*(?<year>\\d{4})\\s*", "Water year ${year}");

    private final Pattern pattern;
    private final String replacement;

    ReturnPeriodFormat(String regex, String replacement) {
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        this.replacement = replacement;
    }

    /**
     * Find the appropriate {@link ReturnPeriodFormat} for the given input string.
     *
     * @param input the return period data as a String
     * @return the appropriate {@link ReturnPeriodFormat} or null if one cannot be matched
     */
    public static ReturnPeriodFormat from(String input) {
        if (input != null) {
            for (ReturnPeriodFormat fmt : values()) {
                if (fmt.pattern.matcher(input).matches()) {
                    return fmt;
                }
            }
        }
        return null;
    }

    /**
     * Given a return period as an input string, generate a standardised output value (standardises use of case and whitespace)
     *
     * @param input the return period data as a String
     * @return the standardised output string, or null if the input string cannot be recognised
     */
    public static String toStandardisedFormat(String input) {
        ReturnPeriodFormat fmt = from(input);
        String value = input;
        if (fmt != null && input != null) {
            value = fmt.pattern.matcher(input).replaceAll(fmt.replacement);
            value = StringUtils.capitalize(value.toLowerCase());
        }
        return value;
    }
}