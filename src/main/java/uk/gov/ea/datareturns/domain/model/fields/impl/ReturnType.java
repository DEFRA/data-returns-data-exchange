package uk.gov.ea.datareturns.domain.model.fields.impl;

import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.jpa.dao.ReturnTypeDao;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.fields.AbstractEntityValue;
import uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist.ReturnTypeAuditor;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledList;

/**
 * The return type describes the type of data being returned.
 *
 * @author Sam Gardner-Dell
 */
public class ReturnType extends AbstractEntityValue<DataSample, uk.gov.ea.datareturns.domain.jpa.entities.ReturnType> {
    @NotBlank(message = MessageCodes.Missing.Rtn_Type)
    @ControlledList(auditor = ReturnTypeAuditor.class, message = MessageCodes.ControlledList.Rtn_Type)
    private final String inputValue;

    /**
     * Instantiates a new Rtn_Type
     *
     * @param inputValue the input value
     */
    public ReturnType(String inputValue) {
        super(ReturnTypeDao.class, inputValue);
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}