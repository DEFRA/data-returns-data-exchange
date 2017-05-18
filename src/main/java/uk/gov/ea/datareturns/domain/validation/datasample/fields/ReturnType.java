package uk.gov.ea.datareturns.domain.validation.datasample.fields;

import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ReturnTypeDao;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleFieldMessageMap;
import uk.gov.ea.datareturns.domain.validation.newmodel.auditors.controlledlist.ReturnTypeAuditorNew;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.AbstractEntityValue;

/**
 * The return type describes the type of data being returned.
 *
 * @author Sam Gardner-Dell
 */
public class ReturnType extends AbstractEntityValue<ReturnTypeDao, uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ReturnType> {
    private static final ReturnTypeDao DAO = EntityDao.getDao(ReturnTypeDao.class);

    @NotBlank(message = DataSampleFieldMessageMap.Missing.Rtn_Type)
    @ControlledList(auditor = ReturnTypeAuditorNew.class, message = DataSampleFieldMessageMap.ControlledList.Rtn_Type)
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