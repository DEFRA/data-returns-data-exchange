package uk.gov.ea.datareturns.domain.model.fields.impl;

import uk.gov.ea.datareturns.domain.jpa.dao.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.QualifierDao;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.fields.AbstractEntityValue;
import uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist.QualifierAuditor;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledList;

/**
 * Qualifies a measurement with additional information to better define the properties of measurement.  E.g. dry weight, wet weight
 *
 * @author Sam Gardner-Dell
 */
public class Qualifier extends AbstractEntityValue<QualifierDao, DataSample, uk.gov.ea.datareturns.domain.jpa.entities.Qualifier> {
    private static final QualifierDao DAO = EntityDao.getDao(QualifierDao.class);

    @ControlledList(auditor = QualifierAuditor.class, message = MessageCodes.ControlledList.Qualifier)
    private final String inputValue;

    /**
     * Instantiates a new Qualifier.
     *
     * @param inputValue the input value
     */
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
