package uk.gov.ea.datareturns.domain.model.fields.impl;

import uk.gov.ea.datareturns.domain.jpa.dao.MethodOrStandardDao;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.fields.AbstractEntityValue;
import uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist.MethodOrStandardAuditor;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledList;

/**
 * The method or standard used for monitoring
 *
 * @author Sam Gardner-Dell
 */
public class MethodOrStandard extends AbstractEntityValue<DataSample, uk.gov.ea.datareturns.domain.jpa.entities.MethodOrStandard> {
    @ControlledList(auditor = MethodOrStandardAuditor.class, message = "{DR9100-Incorrect}")
    private final String inputValue;

    /**
     * Instantiates a new MethodOrStandard
     *
     * @param inputValue the input value
     */
    public MethodOrStandard(String inputValue) {
        super(MethodOrStandardDao.class, inputValue);
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}
