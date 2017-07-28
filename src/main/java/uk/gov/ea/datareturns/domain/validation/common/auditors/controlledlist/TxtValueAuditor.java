package uk.gov.ea.datareturns.domain.validation.common.auditors.controlledlist;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.TextValueDao;
import uk.gov.ea.datareturns.domain.validation.common.constraints.controlledlist.ControlledListAuditor;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Controlled list auditor for the Txt_Value field.
 *
 * @author Sam Gardner-Dell
 */
@Component
public class TxtValueAuditor implements ControlledListAuditor {
    private final TextValueDao textValueDao;

    @Inject public TxtValueAuditor(TextValueDao textValueDao) {
        this.textValueDao = textValueDao;
    }

    /* (non-Javadoc)
     * @see uk.gov.ea.datareturns.domain.validation.model.validation.entityfields.ControlledListAuditor#isValid(java.lang.Object)
     */
    @Override
    public boolean isValid(final Object value) {
        return this.textValueDao.nameOrAliasExists(Key.relaxed(Objects.toString(value, "")));
    }
}