package uk.gov.ea.datareturns.domain.model.fields.impl;

import uk.gov.ea.datareturns.domain.jpa.dao.UnitDao;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.fields.AbstractAliasingEntityValue;
import uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist.UnitAuditor;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledList;

/**
 * The unit or measure used for the given data return
 *
 * @author Sam Gardner-Dell
 */
public class Unit extends AbstractAliasingEntityValue<DataSample, uk.gov.ea.datareturns.domain.jpa.entities.Unit> {
    @ControlledList(auditor = UnitAuditor.class, message = "{DR9050-Incorrect}")
    private final String inputValue;

    /**
     * Instantiates a new Unit.
     *
     * @param inputValue the input value
     */
    public Unit(String inputValue) {
        super(UnitDao.class, inputValue);
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}
