package uk.gov.ea.datareturns.domain.validation.datasample.fields;

import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.QualifierDao;
import uk.gov.ea.datareturns.domain.validation.newmodel.auditors.controlledlist.QualifierAuditorNew;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.AbstractEntityValue;

/**
 * Qualifies a measurement with additional information to better define the properties of measurement.  E.g. dry weight, wet weight
 *
 * @author Sam Gardner-Dell
 */
public class Qualifier extends AbstractEntityValue<QualifierDao, uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Qualifier> {
    private static final QualifierDao DAO = EntityDao.getDao(QualifierDao.class);

    @ControlledList(auditor = QualifierAuditorNew.class, message = "DR9180-Incorrect")
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
