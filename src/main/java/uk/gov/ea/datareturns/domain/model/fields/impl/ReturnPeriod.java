package uk.gov.ea.datareturns.domain.model.fields.impl;

import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.fields.FieldValue;
import uk.gov.ea.datareturns.domain.model.rules.ReturnPeriodFormat;
import uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist.ReturnPeriodAuditor;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledList;

/**
 * Name of date range covering the entire return.
 *
 * @author Sam Gardner-Dell Created by sam on 04/10/16.
 */
public class ReturnPeriod implements FieldValue<DataSample, String> {
    @ControlledList(auditor = ReturnPeriodAuditor.class, message = "{DR9070-Incorrect}")
    private final String inputValue;
    private final String value;

    /**
     * Instantiates a new ReturnPeriod.
     *
     * @param inputValue the input value
     */
    public ReturnPeriod(String inputValue) {
        this.inputValue = inputValue;
        this.value = ReturnPeriodFormat.toStandardisedFormat(inputValue);
    }

    @Override public String getInputValue() {
        return inputValue;
    }

    @Override public String getValue() {
        return this.value;
    }

    @Override public String transform(DataSample record) {
        return this.value;
    }
}
