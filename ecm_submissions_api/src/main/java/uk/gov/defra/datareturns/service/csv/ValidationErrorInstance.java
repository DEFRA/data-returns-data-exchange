package uk.gov.defra.datareturns.service.csv;

import lombok.Value;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Simple object that will be serialized to the response for each validation error encountered
 *
 * @author Sam Gardner-Dell
 */
@Value(staticConstructor = "of")
public class ValidationErrorInstance {
    private final Map<String, String> invalidValues;
    private final Set<Integer> lineNumbers = new TreeSet<>();
}
