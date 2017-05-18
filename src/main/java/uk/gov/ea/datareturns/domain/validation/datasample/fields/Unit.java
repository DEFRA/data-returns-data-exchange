package uk.gov.ea.datareturns.domain.validation.datasample.fields;

import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UnitDao;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleFieldMessageMap;
import uk.gov.ea.datareturns.domain.validation.newmodel.auditors.controlledlist.UnitAuditorNew;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.AbstractAliasingEntityValue;

/**
 * The unit or measure used for the given data return
 *
 * @author Sam Gardner-Dell
 */
public class Unit extends AbstractAliasingEntityValue<uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Unit> {
    private static final UnitDao DAO = EntityDao.getDao(UnitDao.class);

    @ControlledList(auditor = UnitAuditorNew.class, message = DataSampleFieldMessageMap.ControlledList.Unit)
    private final String inputValue;

    /**
     * Instantiates a new Unit.
     *
     * @param inputValue the input value
     */
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