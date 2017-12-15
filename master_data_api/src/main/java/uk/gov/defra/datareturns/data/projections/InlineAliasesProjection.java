package uk.gov.defra.datareturns.data.projections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.AliasedEntity;

import java.util.Set;

/**
 * Projection to display aliases inline as an array.
 */
@Projection(name = "inlineAliases", types = AliasedEntity.class)
public interface InlineAliasesProjection {
    Long getId();

    String getNomenclature();

    @Value("#{target.aliases.![nomenclature]}")
    Set<String> getAliases();
}
