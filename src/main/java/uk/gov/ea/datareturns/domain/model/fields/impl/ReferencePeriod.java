package uk.gov.ea.datareturns.domain.model.fields.impl;

import uk.gov.ea.datareturns.domain.jpa.dao.ReferencePeriodDao;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.fields.AbstractAliasingEntityValue;
import uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist.ReferencePeriodAuditor;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledList;

/**
 * The reference period for the sample describes how the sample was taken - eg, '24 hour total', 'Half hour average'
 *
 * @author Sam Gardner-Dell
 */
public class ReferencePeriod extends AbstractAliasingEntityValue<DataSample, uk.gov.ea.datareturns.domain.jpa.entities.ReferencePeriod> {
    @ControlledList(auditor = ReferencePeriodAuditor.class, message = MessageCodes.ControlledList.Ref_Period)
    private final String inputValue;

    /**
     * Instantiates a new ReferencePeriod
     *
     * @param inputValue the input value
     */
    public ReferencePeriod(String inputValue) {
        super(ReferencePeriodDao.class, inputValue);
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}
