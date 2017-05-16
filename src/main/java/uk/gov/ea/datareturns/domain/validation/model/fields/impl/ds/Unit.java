package uk.gov.ea.datareturns.domain.validation.model.fields.impl.ds;

import com.fasterxml.jackson.annotation.JsonCreator;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UnitDao;
import uk.gov.ea.datareturns.domain.validation.model.DataSample;
import uk.gov.ea.datareturns.domain.validation.model.MessageCodes;
import uk.gov.ea.datareturns.domain.validation.model.fields.AbstractAliasingEntityValue;
import uk.gov.ea.datareturns.domain.validation.model.validation.auditors.controlledlist.UnitAuditor;
import uk.gov.ea.datareturns.domain.validation.model.validation.constraints.controlledlist.ControlledList;

/**
 * The unit or measure used for the given data return
 *
 * @author Sam Gardner-Dell
 */
public class Unit extends AbstractAliasingEntityValue<DataSample, uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Unit> {
    private static final UnitDao DAO = EntityDao.getDao(UnitDao.class);
    @ControlledList(auditor = UnitAuditor.class, message = MessageCodes.ControlledList.Unit)
    private final String inputValue;

    /**
     * Instantiates a new Unit.
     *
     * @param inputValue the input value
     */
    @JsonCreator
    public Unit(String inputValue) {
        super(inputValue);
        this.inputValue = inputValue;
    }

    @Override protected UnitDao getDao() {
        return DAO;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}