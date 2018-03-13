package uk.gov.defra.datareturns.data.projections.parameters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.parameter.Parameter;
import uk.gov.defra.datareturns.data.projections.InlineAliasesProjection;

import java.util.Set;


/**
 * InlineParameterData to display all parameter data with inline aliases.
 *
 * @author Sam Gardner-Dell
 */
@Projection(name = "inlineParameterData", types = Parameter.class)
@SuppressWarnings("unused")
public interface InlineParameterData extends InlineAliasesProjection {
    Long getId();

    String getNomenclature();

    @Value("#{target.aliases.![nomenclature]}")
    Set<String> getAliases();

    String getCas();

    @Value("#{target.type.nomenclature}")
    String getType();
}
