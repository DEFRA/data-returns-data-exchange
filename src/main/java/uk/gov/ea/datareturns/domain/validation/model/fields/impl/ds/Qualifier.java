package uk.gov.ea.datareturns.domain.validation.model.fields.impl.ds;

import com.fasterxml.jackson.annotation.JsonCreator;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.QualifierDao;
import uk.gov.ea.datareturns.domain.validation.model.DataSample;
import uk.gov.ea.datareturns.domain.validation.model.MessageCodes;
import uk.gov.ea.datareturns.domain.validation.model.fields.AbstractEntityValue;
import uk.gov.ea.datareturns.domain.validation.model.validation.auditors.controlledlist.QualifierAuditor;
import uk.gov.ea.datareturns.domain.validation.model.validation.constraints.controlledlist.ControlledList;

/**
 * Qualifies a measurement with additional information to better define the properties of measurement.  E.g. dry weight, wet weight
 *
 * @author Sam Gardner-Dell
 */
public class Qualifier extends AbstractEntityValue<QualifierDao, DataSample, uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Qualifier> {
    private static final QualifierDao DAO = EntityDao.getDao(QualifierDao.class);

    @ControlledList(auditor = QualifierAuditor.class, message = MessageCodes.ControlledList.Qualifier)
    private final String inputValue;

    /**
     * Instantiates a new Qualifier.
     *
     * @param inputValue the input value
     */
    @JsonCreator
    public Qualifier(String inputValue) {
        super(inputValue);
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }

    @Override protected QualifierDao getDao() {
        return DAO;
    }
}
