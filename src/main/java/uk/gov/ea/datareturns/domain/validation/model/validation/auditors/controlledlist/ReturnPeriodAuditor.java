package uk.gov.ea.datareturns.domain.validation.model.validation.auditors.controlledlist;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.validation.model.rules.ReturnPeriodFormat;
import uk.gov.ea.datareturns.domain.validation.model.validation.constraints.controlledlist.ControlledListAuditor;

import java.util.Objects;

/**
 * Controlled list auditor for return periods
 *
 * @author Sam Gardner-Dell
 */
@Component
public class ReturnPeriodAuditor implements ControlledListAuditor {
    /**
     *
     */
    public ReturnPeriodAuditor() {
    }

    /* (non-Javadoc)
     * @see uk.gov.ea.datareturns.domain.validation.model.validation.entityfields.ControlledListAuditor#isValid(java.lang.Object)
     */
    @Override
    public boolean isValid(final Object value) {
        return ReturnPeriodFormat.from(Objects.toString(value, null)) != null;
    }
}