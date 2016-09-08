package uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.ReferencePeriodDao;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Controlled list auditor for reference periods
 *
 * @author Sam Gardner-Dell
 */
@Component
public class ReferencePeriodAuditor implements ControlledListAuditor {
    @Inject
    private ReferencePeriodDao dao;

    /* (non-Javadoc)
     * @see uk.gov.ea.datareturns.domain.model.validation.constraints.ControlledListAuditor#isValid(java.lang.Object)
     */
    @Override
    public boolean isValid(final Object value) {
        return this.dao.nameExistsRelaxed(Objects.toString(value, ""));
    }
}