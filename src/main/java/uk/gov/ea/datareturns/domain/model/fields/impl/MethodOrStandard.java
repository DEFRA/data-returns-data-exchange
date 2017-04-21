package uk.gov.ea.datareturns.domain.model.fields.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.MethodOrStandardDao;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.fields.AbstractEntityValue;
import uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist.MethodOrStandardAuditor;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledList;

/**
 * The method or standard used for monitoring
 *
 * @author Sam Gardner-Dell
 */
public class MethodOrStandard
        extends AbstractEntityValue<MethodOrStandardDao, DataSample, uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.MethodOrStandard> {
    private static final MethodOrStandardDao DAO = EntityDao.getDao(MethodOrStandardDao.class);

    @ControlledList(auditor = MethodOrStandardAuditor.class, message = MessageCodes.ControlledList.MethodOrStandard)
    private final String inputValue;

    /**
     * Instantiates a new MethodOrStandard
     *
     * @param inputValue the input value
     */
    @JsonCreator
    public MethodOrStandard(String inputValue) {
        super(inputValue);
        this.inputValue = inputValue;
    }

    @Override protected MethodOrStandardDao getDao() {
        return DAO;
    }

    @Override public String getInputValue() {
        return inputValue;
    }

}
