package uk.gov.ea.datareturns.domain.model.validation.auditors.dependencies;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import uk.gov.ea.datareturns.domain.model.validation.constraints.dependencies.DependentFieldAuditor;

// TODO: Dependent field validation functionality to be confirmed this week!
public class CommentDefinedForNoValueAuditor implements DependentFieldAuditor {

	@Override
	public boolean isValid(Object primaryFieldValue, Object dependentFieldValue) {
		String primaryField = Objects.toString(primaryFieldValue, null);
		String otherField = Objects.toString(dependentFieldValue, null);
		
		if ("NA".equals(primaryField)) {
			return !StringUtils.isBlank(otherField);
		}
		return true;
	}

}
