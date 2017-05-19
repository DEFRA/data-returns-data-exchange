
package uk.gov.ea.datareturns.domain.validation.datasample.fields;

import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ParameterDao;
import uk.gov.ea.datareturns.domain.validation.newmodel.auditors.controlledlist.ParameterAuditorNew;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.AbstractAliasingEntityValue;

/**
 * The chemical substance or physical parameter being measured
 *
 * @author Sam Gardner-Dell
 */
public class Parameter extends AbstractAliasingEntityValue<uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Parameter> {
    private static final ParameterDao DAO = EntityDao.getDao(ParameterDao.class);

    @NotBlank(message = "DR9030-Missing")
    @ControlledList(auditor = ParameterAuditorNew.class, message = "DR9030-Incorrect")
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
