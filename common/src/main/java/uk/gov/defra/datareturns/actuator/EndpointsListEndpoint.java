package uk.gov.defra.datareturns.actuator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class EndpointsListEndpoint extends AbstractEndpoint<List<Endpoint>> {
    private final List<Endpoint> endpoints;

    @Value("${management.context-path:/}") // default to '/'
    private String managementContextPath;

    @Inject
    public EndpointsListEndpoint(final List<Endpoint> endpoints) {
        super("endpoints");
        this.endpoints = endpoints;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    @Override
    public List<Endpoint> invoke() {
        return endpoints;
    }
}
