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
 * @author Sam Gardner-Dell
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
		return this.instant;
	}

	/**
	 * @return the timeSpecified
	 */
	public boolean isTimeSpecified() {
		return this.timeSpecified;
	}

	/**
	 * @return the parsed
	 */
	public boolean isParsed() {
		return this.parsed;
	}

	@Override
	public String toString() {
		return (this.instant != null) ? this.instant.toString() : this.originalValue;
	}

	public String toStandardisedFormat() {
		String fmt = "";
		if (this.instant != null) {
			if (isTimeSpecified()) {
				fmt = DateTimeFormatter.ofPattern(DateFormat.STANDARD_DATE_TIME_FORMAT)
						.format(LocalDateTime.ofInstant(this.instant, ZoneOffset.UTC));
			} else {
				fmt = DateTimeFormatter.ofPattern(DateFormat.STANDARD_DATE_FORMAT)
						.format(LocalDateTime.ofInstant(this.instant, ZoneOffset.UTC));
			}
		}
		return fmt;
	}

	public static ReturnsDate from(final String value) {
		final ReturnsDate date = new ReturnsDate();
		date.originalValue = value;
		if (value != null) {
			if (value.length() > DateFormat.STANDARD_DATE_FORMAT.length()) {
				final LocalDateTime dateTimeValue = DateFormat.parseDateTime(value);
				if (dateTimeValue != null) {
					date.instant = dateTimeValue.toInstant(ZoneOffset.UTC);
					date.timeSpecified = true;
					date.parsed = true;
				}
			} else {
				final LocalDate dateValue = DateFormat.parseDate(value);
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