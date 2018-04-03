package uk.gov.defra.datareturns.validation.payloads.datasample.rules;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Data Returns Business Rules for Date processing
 *
 * @author Sam Gardner-Dell
 */
public final class DateFormat {
    /**
     * The standard DEP date format - always used for output
     */
    public static final String STANDARD_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * The standard DEP date-time format - always used for output
     */
    private static final String STANDARD_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    /**
     * Date-time Formats recognised by the Data Returns project
     */
    private static final String[] DATE_TIME_FORMATS = {
            STANDARD_DATE_TIME_FORMAT,
            "yyyy-MM-dd HH:mm:ss",
            "dd-MM-yyyy'T'HH:mm:ss",
            "dd-MM-yyyy HH:mm:ss",
            "dd/MM/yyyy'T'HH:mm:ss",
            "dd/MM/yyyy HH:mm:ss"
    };
    /**
     * Date-only formats recognised by the Data Returns project
     */
    private static final String[] DATE_FORMATS = {
            STANDARD_DATE_FORMAT,
            "dd-MM-yyyy",
            "dd/MM/yyyy"
    };

    /**
     * Private utility class constructor
     */
    private DateFormat() {
    }

    /**
     * Attempts to parseJsonArray the given string as a date-only value
     *
     * @param value the value to be parsed
     * @return a valid {@link LocalDate} if the date could be parsed, null otherwise
     */
    public static LocalDate parseDate(final String value) {
        // Formats recognised by the Data Returns project
        LocalDate parsedDate = null;
        for (final String format : DATE_FORMATS) {
            try {
                parsedDate = LocalDate.parse(value, DateTimeFormatter.ofPattern(format));
                // We managed to parseJsonArray a date against one of the recognised formats, break from loop
                break;
            } catch (final DateTimeParseException e) {
                parsedDate = null;
            }
        }
        return parsedDate;
    }

    /**
     * Attempts to parseJsonArray the given string as a date-time value
     *
     * @param value the value to be parsed
     * @return a valid {@link LocalDateTime} if the date could be parsed, null otherwise
     */
    public static LocalDateTime parseDateTime(final String value) {
        LocalDateTime parsedDate = null;
        for (final String format : DATE_TIME_FORMATS) {
            try {
                parsedDate = LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format));
                // We managed to parseJsonArray a date against one of the recognised formats, break from loop
                break;
            } catch (final DateTimeParseException e) {
                parsedDate = null;
            }
        }
        return parsedDate;
    }
}
