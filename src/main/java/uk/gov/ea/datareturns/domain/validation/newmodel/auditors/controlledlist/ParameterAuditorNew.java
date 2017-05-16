package uk.gov.ea.datareturns.domain.validation.newmodel.auditors.controlledlist;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ParameterDao;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.controlledlist.ControlledListAuditor;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Controlled list auditor for parameters
 *
 * @author Sam Gardner-Dell
 */
@Component
public class ParameterAuditorNew implements ControlledListAuditor {
    @Inject
    private ParameterDao parameterDao;

    /**
     *
     */
    public ParameterAuditorNew() {
    }

    /* (non-Javadoc)
     * @see uk.gov.ea.datareturns.domain.validation.model.validation.entityfields.controlledlist.ControlledListAuditor#isValid(java.lang.Object)
     */
    @Override
    public boolean isValid(final Object value) {
        return this.parameterDao.nameOrAliasExists(Key.relaxed(Objects.toString(value, "")));
    }

}
