package uk.gov.ea.datareturns.web.resource.v1.model.common.validation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Used to define the information regarding a field which contributed to a validation error
 *
 * Used to transmit the error value (the user input value),
 * and the resolved value (where there is an input that is mutated, for instanced aliases,
 * but where the resolved value is still invalid (for dependencies for example),
 * for each field name required by the front end for error reporting
 */
@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder(value = {"name", "value", "resolvedValue"})
public class ValidationErrorField {
    @JsonProperty("name")
    private String name;

    @JsonProperty("value")
    private String value;

    @JsonProperty("resolvedValue")
    private String resolvedValue;

    /**
     * @return the name of the field
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the field
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value of the field (exactly as entered by the user)
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value of the field (exactly as entered by the user)
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets resolved value (if available).
     * If the data in this field was resolved against a controlled list then the resolved value shall appear here
     * @return the resolved value
     */
    public String getResolvedValue() {
        return resolvedValue;
    }

    /**
     * Sets the resolved value.
     *
     * @param resolvedValue the resolved value
     */
    public void setResolvedValue(String resolvedValue) {
        this.resolvedValue = resolvedValue;
    }
}
