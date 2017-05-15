package uk.gov.ea.datareturns.domain.validation.landfillmeasurement.fields;

import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ReturnTypeDao;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementMvo;
import uk.gov.ea.datareturns.domain.validation.model.MessageCodes;
import uk.gov.ea.datareturns.domain.validation.model.fields.AbstractEntityValue;
import uk.gov.ea.datareturns.domain.validation.model.validation.auditors.controlledlist.ReturnTypeAuditor;
import uk.gov.ea.datareturns.domain.validation.model.validation.constraints.controlledlist.ControlledList;

/**
 * The return type describes the type of data being returned.
 *
 * @author Sam Gardner-Dell
 */
public class ReturnType extends AbstractEntityValue<ReturnTypeDao, uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ReturnType> {
    private static final ReturnTypeDao DAO = EntityDao.getDao(ReturnTypeDao.class);

    @NotBlank(message = MessageCodes.Missing.Rtn_Type)
    @ControlledList(auditor = ReturnTypeAuditor.class, message = MessageCodes.ControlledList.Rtn_Type)
    private final String inputValue;

    /**
     * Instantiates a new Rtn_Type
     *
     * @param inputValue the input value
     */
    public ReturnType(String inputValue) {
        super(inputValue);
        this.inputValue = inputValue;
    }

    @Override protected ReturnTypeDao getDao() {
        return DAO;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}