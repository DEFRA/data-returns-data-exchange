package uk.gov.ea.datareturns.domain.model.validation;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

/**
 * So that the validation is tiered. The hierarchy validation is only performed is
 * the default validation passes
 * @author Graham Willis
 */
public final class ValidationGroups {
    public interface RecordTier {
    }
    @GroupSequence({ Default.class, RecordTier.class })
    public interface OrderedChecks {
    }
}
