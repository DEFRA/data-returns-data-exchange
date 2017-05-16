package uk.gov.ea.datareturns.web.resource.v1.model.common.validation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ValidationErrorInstance} is created for each unique instance of a particular error.
 * Due to the nature of the data, errors are often repeated multiple times on different rows for the same field values.
 *
 * This class is used to group these errors and avoid repeatedly serializing the same data for every instance of a validation error.
 *
 * @author Sam Gardner-Dell
 */
@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder(value = {"recordIndices", "fields"})
public class ValidationErrorInstance {
    @JsonProperty("recordIndices")
    private List<Integer> recordIndices = new ArrayList<>();
    @JsonProperty("fields")
    private List<ValidationErrorField> fields;

    /**
     * The zero-based record indices where this error occurred
     *
     * @return the record indices
     */
    public List<Integer> getRecordIndices() {
        return recordIndices;
    }

    /**
     * The fields which contributed to this validation error (may be multiple for dependent field validation)
     *
     * @return the fields
     */
    public List<ValidationErrorField> getFields() {
        return fields;
    }

    /**
     * Sets the fields which contributed to this validation error (may be multiple for dependent field validation)
     *
     * @param fields the fields
     */
    public void setFields(List<ValidationErrorField> fields) {
        this.fields = fields;
    }
}