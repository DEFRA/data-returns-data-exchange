package uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields;

import uk.gov.ea.datareturns.domain.validation.common.entityfields.FieldValue;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.constraints.annotations.ValidReturnPeriod;

/**
 * Name of date range covering the entire return.
 *
 * @author Sam Gardner-Dell Created by sam on 04/10/16.
 */
public class ReturnPeriod implements FieldValue<String> {
    public static final String FIELD_NAME = "Rtn_Period";

    @ValidReturnPeriod
    private final String inputValue;

    /**
     * Instantiates a new Rtn_Period.
     *
     * @param inputValue the input value
     */
    public ReturnPeriod(String inputValue) {
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}

