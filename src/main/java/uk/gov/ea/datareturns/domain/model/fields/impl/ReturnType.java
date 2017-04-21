package uk.gov.ea.datareturns.domain.model.fields.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ReturnTypeDao;
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
public class ReturnType extends AbstractEntityValue<ReturnTypeDao, DataSample, uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ReturnType> {
    private static final ReturnTypeDao DAO = EntityDao.getDao(ReturnTypeDao.class);

    @NotBlank(message = MessageCodes.Missing.Rtn_Type)
    @ControlledList(auditor = ReturnTypeAuditor.class, message = MessageCodes.ControlledList.Rtn_Type)
    private final String inputValue;

    /**
     * Instantiates a new Rtn_Type
     *
     * @param inputValue the input value
     */
    @JsonCreator
    public ReturnType(String inputValue) {
        super(inputValue);
        this.inputValue = inputValue;
    }

    @Override protected ReturnTypeDao getDao() {
        return DAO;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}