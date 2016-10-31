package uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.ReleasesAndTransfersDao;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Controlled list auditor for Releases and transfers.
 *
 * @author Sam Gardner-Dell
 */
@Component
public class ReleasesAndTransfersAuditor implements ControlledListAuditor {
    @Inject
    private ReleasesAndTransfersDao dao;

    /**
     *
     */
    public ReleasesAndTransfersAuditor() {

    }

    /* (non-Javadoc)
     * @see uk.gov.ea.datareturns.domain.model.validation.constraints.ControlledListAuditor#isValid(java.lang.Object)
     */
    @Override
    public boolean isValid(final Object value) {
        return this.dao.nameExistsRelaxed(Objects.toString(value, ""));
    }
}