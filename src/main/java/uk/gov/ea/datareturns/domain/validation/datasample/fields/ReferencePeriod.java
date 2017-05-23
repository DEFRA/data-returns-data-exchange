package uk.gov.ea.datareturns.domain.validation.datasample.fields;

import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ReferencePeriodDao;
import uk.gov.ea.datareturns.domain.validation.newmodel.auditors.controlledlist.ReferencePeriodAuditorNew;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.AbstractAliasingEntityValue;

/**
 * The reference period for the sample describes how the sample was taken - eg, '24 hour total', 'Half hour average'
 *
 * @author Sam Gardner-Dell
 */
public class ReferencePeriod extends AbstractAliasingEntityValue<uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ReferencePeriod> {
    public static final String FIELD_NAME = "Ref_Period";
    private static final ReferencePeriodDao DAO = EntityDao.getDao(ReferencePeriodDao.class);
    @ControlledList(auditor = ReferencePeriodAuditorNew.class, message = "DR9090-Incorrect")
    private final String inputValue;

    /**
     * Instantiates a new ReferencePeriod
     *
     * @param inputValue the input value
     */
    public ReferencePeriod(String inputValue) {
        super(inputValue);
        this.inputValue = inputValue;
    }

    @Override protected ReferencePeriodDao getDao() {
        return DAO;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}
