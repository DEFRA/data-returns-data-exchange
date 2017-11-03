package uk.gov.defra.datareturns.validation.payloads.datasample.fields;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Reference to the site at which the measurement is being taken.
 *
 * @author Sam Gardner-Dell
 */
public class SiteName {
    public static final String FIELD_NAME = "Site_Name";

    @NotBlank(message = "DR9110-Missing")
    private final String inputValue;

    /**
     * Instantiates a new Site_Name
     *
     * @param inputValue the input value
     */
    public SiteName(final String inputValue) {
        this.inputValue = inputValue;
    }

    public String getInputValue() {
        return this.inputValue;
    }
}
