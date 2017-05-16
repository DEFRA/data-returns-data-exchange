package uk.gov.ea.datareturns.domain.validation.model.validation.auditors.controlledlist;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.SiteDao;
import uk.gov.ea.datareturns.domain.validation.model.validation.constraints.controlledlist.ControlledListAuditor;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Controlled list auditor for Site Names.
 *
 * @author Sam Gardner-Dell
 */
@Component
public class SiteNameAuditor implements ControlledListAuditor {
    @Inject
    private SiteDao siteDao;

    /* (non-Javadoc)
     * @see uk.gov.ea.datareturns.domain.validation.model.validation.entityfields.ControlledListAuditor#isValid(java.lang.Object)
     */
    @Override
    public boolean isValid(final Object value) {
        return this.siteDao.nameExists(Key.relaxed(Objects.toString(value, "")));
    }
}