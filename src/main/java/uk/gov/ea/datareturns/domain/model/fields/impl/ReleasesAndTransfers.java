package uk.gov.ea.datareturns.domain.model.fields.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ReleasesAndTransfersDao;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.fields.AbstractEntityValue;
import uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist.ReleasesAndTransfersAuditor;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledList;

/**
 * The releases and transfers controlled lists describes the route to emission for
 * pollution inventory returns
 *
 * @author Sam Gardner-Dell
 */
public class ReleasesAndTransfers
        extends AbstractEntityValue<ReleasesAndTransfersDao, DataSample, uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ReleasesAndTransfers> {
    private static final ReleasesAndTransfersDao DAO = EntityDao.getDao(ReleasesAndTransfersDao.class);

    @NotBlank(message = MessageCodes.Missing.Rel_Trans)
    @ControlledList(auditor = ReleasesAndTransfersAuditor.class, message = MessageCodes.ControlledList.Rel_Trans)
    private final String inputValue;

    /**
     * Instantiates a new Rel_Trans
     *
     * @param inputValue the input value
     */
    @JsonCreator
    public ReleasesAndTransfers(String inputValue) {
        super(inputValue);
        this.inputValue = inputValue;
    }

    @Override protected ReleasesAndTransfersDao getDao() {
        return DAO;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}