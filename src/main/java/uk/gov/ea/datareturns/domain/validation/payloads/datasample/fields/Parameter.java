
package uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields;

import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.validation.common.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.common.entityfields.FieldValue;

/**
 * The chemical substance or physical parameter being measured
 *
 * @author Sam Gardner-Dell
 */
public class Parameter implements FieldValue<uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Parameter> {
    public static final String FIELD_NAME = "Parameter";

    @NotBlank(message = "DR9030-Missing")
    @ControlledList(entities = uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Parameter.class, message = "DR9030-Incorrect")
    private final String inputValue;

    /**
     * Instantiates a new Parameter
     *
     * @param inputValue the input value
     */
    public Parameter(String inputValue) {
        this.inputValue = inputValue;
    }
    @Override public String getInputValue() {
        return inputValue;
    }
}
