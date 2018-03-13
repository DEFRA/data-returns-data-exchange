package uk.gov.defra.datareturns.data.projections.parameters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.parameter.Parameter;
import uk.gov.defra.datareturns.data.model.parameter.ParameterGroup;
import uk.gov.defra.datareturns.data.projections.InlineAliasesProjection;

import java.util.Set;


/**
 * InlineParameterData to display all parameter data with inline aliases.
 *
 * @author Sam Gardner-Dell
 */
@Projection(name = "inlineParameterGroupData", types = ParameterGroup.class)
@SuppressWarnings("unused")
public interface InlineParameterGroupData {
    String getNomenclature();

    @Value("#{target.parameters}")
    Set<InlineParameterData> getParameters();
}
