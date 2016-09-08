package uk.gov.ea.datareturns.domain.model.validation.auditors.dependencies;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.model.validation.constraints.dependencies.DependentFieldAuditor;

import java.util.Objects;

/**
 * Auditor to check if the primary field is populated that the dependent field must be empty
 *
 * @author Sam Gardner-Dell
 */
public class PrimaryFieldBlocksDependentAuditor implements DependentFieldAuditor {

    @Override
    public boolean isValid(final Object primaryFieldValue, final Object dependentFieldValue) {
        final String primaryField = Objects.toString(primaryFieldValue, null);
        final String dependentField = Objects.toString(dependentFieldValue, null);

        if (StringUtils.isNotEmpty(primaryField)) {
            return StringUtils.isEmpty(dependentField);
        }
        return true;
    }
}
