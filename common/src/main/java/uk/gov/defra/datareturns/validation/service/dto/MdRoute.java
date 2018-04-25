package uk.gov.defra.datareturns.validation.service.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.UriTemplate;

@Getter
@Setter
public class MdRoute extends MdBaseEntity {
    /**
     * UriTemplate to fetch the subroutes for a given route
     */
    private static final UriTemplate TEMPLATE_SUBROUTES_FOR_ROUTE = new UriTemplate("routes/{routeid}/subroutes");


    /**
     * Retrieve a {@link Link} which may be used to retrieve the subroutes for a given route
     *
     * @param routeId the identifier of the route for which to return subroutes.
     * @return a {@link Link} which may be used to retrieve a list of subroutes from the master data API.
     */
    public static Link subroutesLink(final Object routeId) {
        return new Link(TEMPLATE_SUBROUTES_FOR_ROUTE, "subroutes").expand(routeId);
    }
}
