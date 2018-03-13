package uk.gov.defra.datareturns.data.projections.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import uk.gov.defra.datareturns.data.model.route.Route;

import java.util.Set;


/**
 * InlineRegimeObligationData to display all route data inline.
 *
 * @author Sam Gardner-Dell
 */
@Projection(name = "inlineRouteData", types = Route.class)
@SuppressWarnings("unused")
public interface InlineRouteData {

    String getNomenclature();

    @Value("#{target.subroutes.![nomenclature]}")
    Set<String> getSubroutes();
}
