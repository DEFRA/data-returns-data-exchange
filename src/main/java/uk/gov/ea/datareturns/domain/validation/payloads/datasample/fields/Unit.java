package uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields;

import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UnitDao;
import uk.gov.ea.datareturns.domain.validation.common.auditors.controlledlist.UnitAuditor;
import uk.gov.ea.datareturns.domain.validation.common.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.common.entityfields.AbstractAliasingEntityValue;

/**
 * The unit or measure used for the given data return
 *
 * @author Sam Gardner-Dell
 */
public class Unit extends AbstractAliasingEntityValue<uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Unit> {
    public static final String FIELD_NAME = "Unit";
    private static final UnitDao DAO = EntityDao.getDao(UnitDao.class);

    @ControlledList(auditor = UnitAuditor.class, message = "DR9050-Incorrect")
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