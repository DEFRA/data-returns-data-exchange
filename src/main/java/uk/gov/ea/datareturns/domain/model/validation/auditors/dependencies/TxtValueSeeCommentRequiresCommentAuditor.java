package uk.gov.ea.datareturns.domain.model.validation.auditors.dependencies;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.model.validation.constraints.dependencies.DependentFieldAuditor;

import java.util.Objects;

/**
 * Txt_Value can contain "See comment" which means a Comment must be present
 *
 * @author Sam Gardner-Dell
 */
public class TxtValueSeeCommentRequiresCommentAuditor implements DependentFieldAuditor {
    private static final String SEE_COMMENT_VALUE = "See comment";

    @Override
    public boolean isValid(final Object textValueFieldData, final Object commentFieldData) {
        final String txtValueField = Objects.toString(textValueFieldData, null);
        final String commentField = Objects.toString(commentFieldData, null);

        return !SEE_COMMENT_VALUE.equalsIgnoreCase(txtValueField) || StringUtils.isNotBlank(commentField);
    }
}
