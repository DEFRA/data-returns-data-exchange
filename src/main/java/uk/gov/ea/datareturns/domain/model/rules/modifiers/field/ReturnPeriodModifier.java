package uk.gov.ea.datareturns.domain.model.rules.modifiers.field;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.model.rules.ReturnPeriodFormat;

import java.util.Objects;

/**
 * Substitute return periods with the controlled list value using the proper case
 */
@Component
public class ReturnPeriodModifier implements EntityModifier {
    @Override
    public Object doModify(Object input) {
        return ReturnPeriodFormat.toStandardisedFormat(Objects.toString(input, null));
    }
}
