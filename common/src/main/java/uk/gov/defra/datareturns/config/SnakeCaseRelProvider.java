package uk.gov.defra.datareturns.config;

import com.google.common.base.CaseFormat;
import org.atteo.evo.inflector.English;
import org.springframework.hateoas.RelProvider;

public class SnakeCaseRelProvider implements RelProvider {
    @Override
    public String getItemResourceRelFor(Class<?> type) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, type.getSimpleName());
    }

    @Override
    public String getCollectionResourceRelFor(Class<?> type) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, English.plural(type.getSimpleName()));
    }

    @Override
    public boolean supports(Class<?> delimiter) {
        return true;
    }
}
