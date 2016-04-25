/**
 * 
 */
package uk.gov.ea.datareturns.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import uk.gov.ea.datareturns.domain.model.rules.DateFormat;

/**
 * 
 * 
 * @author sam
 *
 */
public class ReturnsDate {

	private Instant instant;
	
	private boolean timeSpecified;
	
	private boolean parsed;
	
	private String originalValue;
	
	private ReturnsDate() {
		
	}
	
	/**
	 * @return the instant
	 */
	public Instant getInstant() {
		return instant;
	}

	/**
	 * @return the timeSpecified
	 */
	public boolean isTimeSpecified() {
		return timeSpecified;
	}
	
	/**
	 * @return the parsed
	 */
	public boolean isParsed() {
		return parsed;
	}

	@Override
	public String toString() {
		return (instant != null) ? instant.toString() : originalValue;
	}
	
	public String toStandardisedFormat() {
		String fmt = "";
		if (instant != null) {
			if (timeSpecified) {
				fmt = DateTimeFormatter.ofPattern(DateFormat.STANDARD_DATE_TIME_FORMAT).format(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
			} else {
				fmt = DateTimeFormatter.ofPattern(DateFormat.STANDARD_DATE_FORMAT).format(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
			}
		}
		return fmt;
	}

	public static ReturnsDate from(String value) {
		ReturnsDate date = new ReturnsDate();
		date.originalValue = value;
		if (value != null) {
			if (value.length() > DateFormat.STANDARD_DATE_FORMAT.length()) {
				LocalDateTime dateTimeValue = DateFormat.parseDateTime(value);
				if (dateTimeValue != null) {
					date.instant = dateTimeValue.toInstant(ZoneOffset.UTC);
					date.timeSpecified = true;
					date.parsed = true;
				}
			} else {
				LocalDate dateValue = DateFormat.parseDate(value);
				if (dateValue != null) {
					date.instant = dateValue.atStartOfDay().toInstant(ZoneOffset.UTC);
					date.timeSpecified = false;
					date.parsed = true;
				}
			}
		}
		return date;
	}
}