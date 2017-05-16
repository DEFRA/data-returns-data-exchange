package uk.gov.ea.datareturns.web.resource.v1.model.common.validation;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a specific type/category of validation error.
 *
 * @author Sam Gardner-Dell
 */
@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder(value = { "errorCode", "errorType", "errorMessage" })
public class ValidationErrorType {
    @JsonProperty("errorCode")
    private int errorCode;

    @JsonProperty("errorType")
    private String errorType;

    @JsonProperty("errorMessage")
    private String errorMessage;

    /**
     * Internal Map to store a list of record indexes which contain the same validation error
     */
    @JsonIgnore
    private final Map<String, ValidationErrorInstance> errorMap = new HashMap<>();

    /**
     * Create a new ValidationError instance
     */
    public ValidationErrorType() {
    }

    /**
     * Gets error type.
     *
     * @return the errorType
     */
    public String getErrorType() {
        return this.errorType;
    }

    /**
     * Sets error type.
     *
     * @param errorType the errorType to set
     */
    public void setErrorType(final String errorType) {
        this.errorType = errorType;
    }

    /**
     * Gets error code.
     *
     * @return the errorCode
     */
    public int getErrorCode() {
        return this.errorCode;
    }

    /**
     * Sets error code.
     *
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets error message.
     *
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Sets error message.
     *
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Gets the set of unique validation error instances for this type/category.
     *
     * @return the set of unique validation error instances for this type/category
     */
    @JsonGetter("instances")
    public List<ValidationErrorInstance> getInstances() {
        return new ArrayList<>(errorMap.values());
    }

    /**
     * Set the list of unique validation error instances for this type/category
     *
     * @param instances the instances
     */
    @JsonSetter("instances")
    public void setInstances(List<ValidationErrorInstance> instances) {
        this.errorMap.clear();
        instances.forEach(instance -> {
            String mapKey = toMapKey(instance.getFields());
            this.errorMap.put(mapKey, instance);
        });
    }

    /**
     * Add a new error instance for the given record index and list of field data within the record that caused the error.
     *
     * If this combination of field data has been observed before then the recordIndex is simply added to the existing data, otherwise
     * a new {@link ValidationErrorInstance} is created
     *
     * @param recordIndex the (zero based) index of the record which caused the validation error
     * @param fieldData the field data that caused the validation error.
     */
    public void addErrorInstance(int recordIndex, List<ValidationErrorField> fieldData) {
        // Generate a map key from the field name and field value (thus we group similar errors)
        final String mapKey = toMapKey(fieldData);
        ValidationErrorInstance instance = errorMap.get(mapKey);
        if (instance == null) {
            instance = new ValidationErrorInstance();
            instance.setFields(fieldData);
        }
        instance.getRecordIndices().add(recordIndex);
        errorMap.put(mapKey, instance);
    }

    /**
     * Gets all unique error instances which include the given record index
     *
     * @param recordIndex the record index
     * @return the instances for record index
     */
    @JsonIgnore
    public List<ValidationErrorInstance> getInstancesForRecordIndex(int recordIndex) {
        return errorMap.values()
                .stream()
                .filter(e -> e.getRecordIndices().contains(recordIndex))
                .collect(Collectors.toList());
    }

    /**
     * Count all errors across all unique error instances
     *
     * @return the count of all errors for this error type/category
     */
    public long countAllErrors() {
        return errorMap.values().stream().mapToInt(e -> e.getRecordIndices().size()).sum();
    }


    /**
     * Generate a map key from the given List of field data
     *
     * @param fieldData the field data which caused a validation error
     * @return a map key which uniquely identifies this set of field data
     */
    private static String toMapKey(List<ValidationErrorField> fieldData) {
        return fieldData.stream().map(f -> f.getName() + "-" + f.getValue()).collect(Collectors.joining("_"));
    }
}