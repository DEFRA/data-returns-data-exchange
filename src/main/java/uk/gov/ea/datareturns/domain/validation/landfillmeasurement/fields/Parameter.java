
package uk.gov.ea.datareturns.domain.validation.landfillmeasurement.fields;

import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ParameterDao;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementMvo;
import uk.gov.ea.datareturns.domain.validation.model.MessageCodes;
import uk.gov.ea.datareturns.domain.validation.model.fields.AbstractAliasingEntityValue;
import uk.gov.ea.datareturns.domain.validation.model.validation.auditors.controlledlist.ParameterAuditor;
import uk.gov.ea.datareturns.domain.validation.model.validation.constraints.controlledlist.ControlledList;

/**
 * The chemical substance or physical parameter being measured
 *
 * @author Sam Gardner-Dell
 */
public class Parameter extends AbstractAliasingEntityValue<uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Parameter> {
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
