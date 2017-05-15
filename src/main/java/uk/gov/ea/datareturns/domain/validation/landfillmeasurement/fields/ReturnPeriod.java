package uk.gov.ea.datareturns.domain.validation.landfillmeasurement.fields;

import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementMvo;
import uk.gov.ea.datareturns.domain.validation.model.MessageCodes;
import uk.gov.ea.datareturns.domain.validation.model.fields.FieldValue;
import uk.gov.ea.datareturns.domain.validation.model.validation.auditors.controlledlist.ReturnPeriodAuditor;
import uk.gov.ea.datareturns.domain.validation.model.validation.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.rules.ReturnPeriodFormat;

/**
 * Name of date range covering the entire return.
 *
 * @author Sam Gardner-Dell Created by sam on 04/10/16.
 */
public class ReturnPeriod implements FieldValue<String> {
    @ControlledList(auditor = ReturnPeriodAuditor.class, message = MessageCodes.ControlledList.Rtn_Period)
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

