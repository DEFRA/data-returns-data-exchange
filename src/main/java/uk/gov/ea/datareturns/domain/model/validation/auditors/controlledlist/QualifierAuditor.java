package uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.QualifierDao;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Controlled list auditor for the qualifier field.  Checks qualifiers against the controlled list.
 *
 * @author Sam Gardner-Dell
 */
@Component
public class QualifierAuditor implements ControlledListAuditor {
    @Inject
    private QualifierDao qualifierDao;

    /**
     *
     */
    public QualifierAuditor() {
    }

    /* (non-Javadoc)
     * @see uk.gov.ea.datareturns.domain.model.validation.constraints.ControlledListAuditor#isValid(java.lang.Object)
     */
    @Override
    public boolean isValid(final Object value) {
        return this.qualifierDao.nameExists(Key.relaxed(Objects.toString(value, "")));
    }
}
