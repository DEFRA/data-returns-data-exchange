package uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields;

import uk.gov.ea.datareturns.domain.validation.common.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.common.entityfields.FieldValue;

/**
 * The method or standard used for monitoring
 *
 * @author Sam Gardner-Dell
 */
public class MethodOrStandard
        implements FieldValue<uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.MethodOrStandard> {
    public static final String FIELD_NAME = "Meth_Stand";

    @ControlledList(entities = uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.MethodOrStandard.class, message = "DR9100-Incorrect")
    private final String inputValue;

    /**
     * Instantiates a new MethodOrStandard
     *
     * @param inputValue the input value
     */
    public MethodOrStandard(String inputValue) {
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}
