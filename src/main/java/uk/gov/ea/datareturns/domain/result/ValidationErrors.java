package uk.gov.ea.datareturns.domain.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Container for validation error detail found in the validated model
 *
 * @author Sam Gardner-Dell
 */
public class ValidationErrors {
    @JsonProperty("validationErrors")
    private final List<ValidationError> errors = Collections.synchronizedList(new ArrayList<>());

    /**
     * Add a new validation error into the list of validation errors
     *
     * @param error the validation error to be added
     */
    public void addError(final ValidationError error) {
        this.errors.add(error);
    }

    /**
     * Check if a validation error exists
     *
     * @return true if a validation error exists, false otherwise
     */
    @JsonIgnore
    public boolean isValid() {
        return this.errors.isEmpty();
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}