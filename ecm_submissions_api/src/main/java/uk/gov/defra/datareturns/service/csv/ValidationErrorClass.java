package uk.gov.defra.datareturns.service.csv;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple object that will be serialized to the response for each validation error encountered
 *
 * @author Sam Gardner-Dell
 */
@Value(staticConstructor = "of")
@EqualsAndHashCode(of = "errorClass")
public class ValidationErrorClass {
    private final String errorClass;
    private final String detail;
    @JsonIgnore
    private final Map<String, ValidationErrorInstance> instancesByErrorValue = new HashMap<>();

    @JsonProperty("instances")
    public Collection<ValidationErrorInstance> getInstances() {
        final List<ValidationErrorInstance> instances = new ArrayList<>(instancesByErrorValue.values());
        instances.sort((a, b) -> {
            final Integer instanceAFirstLineNumber = a.getLineNumbers().stream().findFirst().orElse(Integer.MAX_VALUE);
            final Integer instanceBFirstLineNumber = b.getLineNumbers().stream().findFirst().orElse(Integer.MAX_VALUE);
            return instanceAFirstLineNumber.compareTo(instanceBFirstLineNumber);
        });
        return instances;
    }
}
