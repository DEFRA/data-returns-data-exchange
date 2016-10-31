package uk.gov.ea.datareturns.domain.model.fields.impl;

import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.jpa.dao.ReleasesAndTransfersDao;
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
public class ReleasesAndTransfers extends AbstractEntityValue<DataSample, uk.gov.ea.datareturns.domain.jpa.entities.ReleasesAndTransfers> {
    @NotBlank(message = MessageCodes.Missing.Rel_Trans)
    @ControlledList(auditor = ReleasesAndTransfersAuditor.class, message = MessageCodes.ControlledList.Rel_Trans)
    private final String inputValue;

    /**
     * Instantiates a new Rel_Trans
     *
     * @param inputValue the input value
     */
    public ReleasesAndTransfers(String inputValue) {
        super(ReleasesAndTransfersDao.class, inputValue);
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}