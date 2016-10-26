package uk.gov.ea.datareturns.domain.model.fields.impl;

import uk.gov.ea.datareturns.domain.jpa.dao.TextValueDao;
import uk.gov.ea.datareturns.domain.jpa.entities.TextValue;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.fields.AbstractAliasingEntityValue;
import uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist.TxtValueAuditor;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledList;

/**
 * Models measurements/observations returned as text such as true, false, yes and no
 *
 * @author Sam Gardner-Dell
 */
public class TxtValue extends AbstractAliasingEntityValue<DataSample, TextValue> {
    @ControlledList(auditor = TxtValueAuditor.class, message = MessageCodes.ControlledList.Txt_Value)
    private final String inputValue;

    /**
     * Instantiates a new Txt_Value.
     *
     * @param inputValue the input value
     */
    public TxtValue(String inputValue) {
        super(TextValueDao.class, inputValue);
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}
