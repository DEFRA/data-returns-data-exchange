package uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields;

import uk.gov.ea.datareturns.domain.validation.common.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.common.entityfields.FieldValue;

/**
 * The unit or measure used for the given data return
 *
 * @author Sam Gardner-Dell
 */
public class Unit implements FieldValue<uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Unit> {
    public static final String FIELD_NAME = "Unit";

    @ControlledList(entities = uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Unit.class, message = "DR9050-Incorrect")
    private final String inputValue;

    /**
     * Instantiates a new Unit.
     *
     * @param inputValue the input value
     */
    public Unit(String inputValue) {
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}

