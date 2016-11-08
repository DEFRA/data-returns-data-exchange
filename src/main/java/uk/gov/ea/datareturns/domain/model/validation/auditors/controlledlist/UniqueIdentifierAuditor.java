package uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.service.UniqueIdentifierService;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Controlled list value for unique identifiers
 *
 * @author Sam Gardner-Dell
 */
@Component
public class UniqueIdentifierAuditor implements ControlledListAuditor {
    @Inject
    UniqueIdentifierService dao;
    /* (non-Javadoc)
     * @see uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledListAuditor#isValid(java.lang.Object)
     */
    @Override
    public boolean isValid(final Object value) {
        return this.dao.uniqueIdentifierExists(Objects.toString(value, ""));
    }
}
