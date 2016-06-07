package uk.gov.ea.datareturns.domain.model.validation.auditors.dependencies;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import uk.gov.ea.datareturns.domain.model.validation.constraints.dependencies.DependentFieldAuditor;

/**
 * Sample auditor for dependent field validation - rules to be confirmed soon!
 *
 * @author Sam Gardner-Dell
 */
// TODO: Dependent field validation functionality to be confirmed this week!
public class CommentDefinedForNoValueAuditor implements DependentFieldAuditor {

	@Override
	public boolean isValid(final Object primaryFieldValue, final Object dependentFieldValue) {
		final String primaryField = Objects.toString(primaryFieldValue, null);
		final String otherField = Objects.toString(dependentFieldValue, null);

		if ("NA".equals(primaryField)) {
			return !StringUtils.isBlank(otherField);
		}
		return true;
	}
}
