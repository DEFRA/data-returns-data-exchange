package uk.gov.defra.datareturns.data.projections.regimeobligations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.parameter.Parameter;
import uk.gov.defra.datareturns.data.model.regimeobligation.RegimeObligation;
import uk.gov.defra.datareturns.data.projections.InlineAliasesProjection;
import uk.gov.defra.datareturns.data.projections.parameters.InlineParameterGroupData;
import uk.gov.defra.datareturns.data.projections.route.InlineRouteData;

import java.util.Set;


/**
 * InlineRegimeObligationData to display all regime obligation data.
 *
 * @author Sam Gardner-Dell
 */
@Projection(name = "inlineRegimeObligationData", types = RegimeObligation.class)
@SuppressWarnings("unused")
public interface InlineRegimeObligationData {
    Long getId();

    String getNomenclature();

    @Value("#{target.route}")
    InlineRouteData getRoute();


    @Value("#{target.parameterGroups}")
    Set<InlineParameterGroupData> getParameterGroups();
}
