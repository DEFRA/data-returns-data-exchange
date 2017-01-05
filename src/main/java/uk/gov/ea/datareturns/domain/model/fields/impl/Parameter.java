
package uk.gov.ea.datareturns.domain.model.fields.impl;

import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.ParameterDao;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.fields.AbstractAliasingEntityValue;
import uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist.ParameterAuditor;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledList;

/**
 * The chemical substance or physical parameter being measured
 *
 * @author Sam Gardner-Dell
 */
public class Parameter extends AbstractAliasingEntityValue<DataSample, uk.gov.ea.datareturns.domain.jpa.entities.Parameter> {
    private static final ParameterDao DAO = EntityDao.getDao(ParameterDao.class);

    @NotBlank(message = MessageCodes.Missing.Parameter)
    @ControlledList(auditor = ParameterAuditor.class, message = MessageCodes.ControlledList.Parameter)
    private final String inputValue;

    /**
     * Instantiates a new Parameter
     *
     * @param inputValue the input value
     */
    public Parameter(String inputValue) {
        super(inputValue);
        this.inputValue = inputValue;
    }

    @Override protected ParameterDao getDao() {
        return DAO;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}
