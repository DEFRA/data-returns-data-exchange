package uk.gov.ea.datareturns.domain.validation.datasample.fields;

import uk.gov.ea.datareturns.domain.validation.newmodel.auditors.controlledlist.ReturnPeriodAuditorNew;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.FieldValue;
import uk.gov.ea.datareturns.domain.validation.newmodel.rules.ReturnPeriodFormat;

/**
 * Name of date range covering the entire return.
 *
 * @author Sam Gardner-Dell Created by sam on 04/10/16.
 */
public class ReturnPeriod implements FieldValue<String> {
    public static final String FIELD_NAME = "Rtn_Period";
    @ControlledList(auditor = ReturnPeriodAuditorNew.class, message = "DR9070-Incorrect")
    private final String inputValue;
    private final String value;

    /**
     * Instantiates a new Rtn_Period.
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

}

