package uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields;

import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.MethodOrStandardDao;
import uk.gov.ea.datareturns.domain.validation.common.auditors.controlledlist.MethodOrStandardAuditor;
import uk.gov.ea.datareturns.domain.validation.common.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.common.entityfields.AbstractEntityValue;

/**
 * The method or standard used for monitoring
 *
 * @author Sam Gardner-Dell
 */
public class MethodOrStandard
        extends AbstractEntityValue<MethodOrStandardDao, uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.MethodOrStandard> {
    private static final MethodOrStandardDao DAO = EntityDao.getDao(MethodOrStandardDao.class);

    public static final String FIELD_NAME = "Meth_Stand";

    @ControlledList(auditor = MethodOrStandardAuditor.class, message = "DR9100-Incorrect")
    private final String inputValue;

    /**
     * Instantiates a new MethodOrStandard
     *
     * @param inputValue the input value
     */
    public MethodOrStandard(String inputValue) {
        super(inputValue);
        this.inputValue = inputValue;
    }

    @Override protected MethodOrStandardDao getDao() {
        return DAO;
    }

    @Override public String getInputValue() {
        return inputValue;
    }

}
