package uk.gov.ea.datareturns.domain.model.fields.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
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
    private static final TextValueDao DAO = EntityDao.getDao(TextValueDao.class);
    @ControlledList(auditor = TxtValueAuditor.class, message = MessageCodes.ControlledList.Txt_Value)
    private final String inputValue;

    /**
     * Instantiates a new Txt_Value.
     *
     * @param inputValue the input value
     */
    @JsonCreator
    public TxtValue(String inputValue) {
        super(inputValue);
        this.inputValue = inputValue;
    }

    @Override protected TextValueDao getDao() {
        return DAO;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}
