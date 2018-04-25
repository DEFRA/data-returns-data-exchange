package uk.gov.defra.datareturns.service.csv;

import lombok.Value;

/**
 * Simple object that will be serialized to the response for each validation error encountered
 *
 * @author Sam Gardner-Dell
 */
@Value(staticConstructor = "of")
public class CsvStructureError {
    private final String message;
    private final String detail;
    private final Integer lineNumber;
}
