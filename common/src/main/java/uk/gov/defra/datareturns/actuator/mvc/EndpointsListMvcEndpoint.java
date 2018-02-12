package uk.gov.defra.datareturns.actuator.mvc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.defra.datareturns.actuator.EndpointsListEndpoint;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Component
public class EndpointsListMvcEndpoint extends EndpointMvcAdapter {

    @Value("${management.context-path:/}") // default to '/'
    private String managementContextPath;

    private final EndpointsListEndpoint delegate;

    @Inject
    public EndpointsListMvcEndpoint(final EndpointsListEndpoint delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override public Object invoke() {
        return this.delegate.getEndpoints().stream()
                .map(e -> new EndpointResource(managementContextPath, e))
                .collect(Collectors.toSet());
    }

    @RequestMapping(value = "/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Set<EndpointResource> filter(@RequestParam(required = false) final Boolean enabled,
            @RequestParam(required = false) final Boolean sensitive) {
        final Predicate<Endpoint> isEnabled = endpoint -> matches(endpoint::isEnabled, ofNullable(enabled));
        final Predicate<Endpoint> isSensitive = endpoint -> matches(endpoint::isSensitive, ofNullable(sensitive));

        return this.delegate.getEndpoints().stream()
                .filter(isEnabled.and(isSensitive))
                .map(e -> new EndpointResource(managementContextPath, e))
                .collect(Collectors.toSet());
    }

    private <T> boolean matches(final Supplier<T> supplier, final Optional<T> value) {
        return !value.isPresent() || supplier.get().equals(value.get());
    }

    public static class EndpointResource extends ResourceSupport {
        private final String managementContextPath;
        private final Endpoint endpoint;

        public EndpointResource(final String managementContextPath, final Endpoint endpoint) {
            this.managementContextPath = managementContextPath;
            this.endpoint = endpoint;

            if (endpoint.isEnabled()) {
                final UriComponentsBuilder path = ServletUriComponentsBuilder.fromCurrentServletMapping()
                        .path(this.managementContextPath)
                        .pathSegment(endpoint.getId());

                this.add(new Link(path.build().toUriString(), endpoint.getId()));
            }
        }

        public Endpoint getEndpoint() {
            return endpoint;
        }
    }

}
