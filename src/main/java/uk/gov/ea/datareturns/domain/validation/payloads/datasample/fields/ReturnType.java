package uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields;

import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.validation.common.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.common.entityfields.FieldValue;

/**
 * The return type describes the type of data being returned.
 *
 * @author Sam Gardner-Dell
 */
public class ReturnType implements FieldValue<uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ReturnType> {
    public static final String FIELD_NAME = "Rtn_Type";

    @NotBlank(message = "DR9010-Missing")
    @ControlledList(entities = uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ReturnType.class, message = "DR9010-Incorrect")
    private final String inputValue;

    /**
     * Instantiates a new Rtn_Type
     *
     * @param inputValue the input value
     */
    public ReturnType(String inputValue) {
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}