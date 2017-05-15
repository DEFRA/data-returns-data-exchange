package uk.gov.ea.datareturns.domain.validation.newmodel.auditors.controlledlist;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.QualifierDao;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.controlledlist.ControlledListAuditor;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Controlled list auditor for the qualifier field.  Checks qualifiers against the controlled list.
 *
 * @author Sam Gardner-Dell
 */
@Component
public class QualifierAuditorNew implements ControlledListAuditor {
    @Inject
    private QualifierDao qualifierDao;

    /**
     *
     */
    public QualifierAuditorNew() {
    }

    /* (non-Javadoc)
     * @see uk.gov.ea.datareturns.domain.validation.model.validation.entityfields.ControlledListAuditor#isValid(java.lang.Object)
     */
    @Override
    public boolean isValid(final Object value) {
        return this.qualifierDao.nameExists(Key.relaxed(Objects.toString(value, "")));
    }
}
