package uk.gov.ea.datareturns.domain.model;

import uk.gov.ea.datareturns.domain.model.rules.DateFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Models a date/time specified in the returns data.
 *
 * @author Sam Gardner-Dell
 */
public class ReturnsDate {
    /** The {@link Instant} representing the date that was specified */
    private Instant instant;
    /** Flag to store if the time was specified as part of the date */
    private boolean timeSpecified;
    /** Flag to store if the date/time was successfully parsed when the file was read */
    private boolean parsed;
    /** The original {@link String} representation of the date/time */
    private String originalValue;

    /**
     * Default private constructor for a {@link ReturnsDate}
     * To create a new {@link ReturnsDate} see the static {@link ReturnsDate#from(String)} method
     */
    private ReturnsDate() {
    }

    /**
     * Retrieve the underlying {@link Instant} representation of the date/time
     *
     * @return the instant
     */
    public Instant getInstant() {
        return this.instant;
    }

    /**
     * Was the time portion of the date specified in the original String
     *
     * @return the timeSpecified true if the time was specified, false otherwise
     */
    public boolean isTimeSpecified() {
        return this.timeSpecified;
    }

    /**
     * Was the date/time String parsed successfully
     *
     * @return the parsed true if the String representation was parsed successfully, false otherwise.
     */
    public boolean isParsed() {
        return this.parsed;
    }

    /**
     * Returns the String representation of this {@link ReturnsDate} as originally entered
     *
     * @return a String representation of this {@link ReturnsDate} as originally entered
     */
    @Override
    public String toString() {
        return this.originalValue;
    }

    /**
     * Retrieve the standardised String form of this {@link ReturnsDate}
     *
     * @return the date/time information in standard format.
     */
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

    public String getOriginalValue() {
        return originalValue;
    }

    /**
     * Create a new {@link ReturnsDate} for the given String
     *
     * @param value the String to attempt to parse to create a ReturnsDate.
     * @return a {@link ReturnsDate} object to represent the String value.  Note that this does not guarantee that the String
     * was parsed correctly.  Call the {@link ReturnsDate#isParsed()} method to determine if the String was parsed successfully.
     */
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