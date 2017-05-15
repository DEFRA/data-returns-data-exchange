package uk.gov.ea.datareturns.domain.validation.landfillmeasurement.fields;

import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.field.ValidMonitoringDate;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.FieldValue;
import uk.gov.ea.datareturns.domain.validation.newmodel.rules.DateFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Monitoring date/time. This is the date and (optionally) time (eg, for a spot sample).
 * If monitoring for a period of time this is the date/time at the end of the monitoring period
 *
 * @author Sam Gardner-Dell
 */
@ValidMonitoringDate
public class MonitoringDate implements FieldValue<Instant> {
    /** The {@link Instant} representing the date that was specified */
    private Instant instant;
    /** Flag to store if the time was specified as part of the date */
    private boolean timeSpecified;
    /** Flag to store if the date/time was successfully parsed when the file was read */
    private boolean parsed;
    /** The original {@link String} representation of the date/time */
    private String inputValue;

    /**
     * Create a new {@link MonitoringDate} for the given String
     *
     * @param monitoringDate the String to attempt to parse to create a MonitoringDate.
     */
    public MonitoringDate(final String monitoringDate) {
        this.inputValue = monitoringDate;
        if (this.inputValue != null) {
            if (this.inputValue.length() > DateFormat.STANDARD_DATE_FORMAT.length()) {
                final LocalDateTime dateTimeValue = DateFormat.parseDateTime(this.inputValue);
                if (dateTimeValue != null) {
                    this.instant = dateTimeValue.toInstant(ZoneOffset.UTC);
                    this.timeSpecified = true;
                    this.parsed = true;
                }
            } else {
                final LocalDate dateValue = DateFormat.parseDate(this.inputValue);
                if (dateValue != null) {
                    this.instant = dateValue.atStartOfDay().toInstant(ZoneOffset.UTC);
                    this.timeSpecified = false;
                    this.parsed = true;
                }
            }
        }
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
     * Returns the String representation of this {@link MonitoringDate} as originally entered
     *
     * @return a String representation of this {@link MonitoringDate} as originally entered
     */
    @Override
    public String toString() {
        return this.inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }

    public Instant getValue() {
        return this.instant;
    }

}