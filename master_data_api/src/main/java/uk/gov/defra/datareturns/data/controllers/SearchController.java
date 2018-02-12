package uk.gov.defra.datareturns.data.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.RepositorySearchesResource;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.defra.datareturns.data.RepositoryLookupService;
import uk.gov.defra.datareturns.data.BaseRepository;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Search controller to enable full text search of master data entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestController
@RequestMapping(value = "/{repository}/search")
@RequiredArgsConstructor
public class SearchController implements ResourceProcessor<RepositorySearchesResource> {
    private final RepositoryLookupService repositories;
    private final EntityLinks entityLinks;

    /**
     * Perform a full text search lookup for the given terms
     *
     * @param repositoryName the path variable giving the name of the entity to perform the search on
     * @param q              The keywords to search for
     * @return the set of resources which match the given terms
     */
    @GetMapping("/lookup")
    public ResponseEntity<Resources<?>> search(
            @PathVariable("repository") final String repositoryName,
            @NotNull @RequestParam final String q
    ) {
        final Class<?> domainClass = repositories.getDomainType(repositoryName);
        final BaseRepository<?, ?> repository = repositories.getRepository(domainClass);
        final String[] searchableFields = repositories.getSearchableFields(domainClass);
        if (searchableFields != null) {
            final List<?> results = repository.findByKeyword(q, searchableFields);
            final Resources<?> resource = new Resources<>(results);
            return new ResponseEntity<>(resource, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Define which entities will have the lookup function available
     *
     * @param resource the spring-data-rest {@link RepositorySearchesResource} object
     * @return the spring-data-rest {@link RepositorySearchesResource} object
     */
    @Override
    public RepositorySearchesResource process(final RepositorySearchesResource resource) {
        if (repositories.getSearchableFields(resource.getDomainType()) != null) {
            final LinkBuilder lb = entityLinks.linkFor(resource.getDomainType(), "q");
            resource.add(new Link(lb.toString() + "/search/lookup{?q}", "lookup"));
        }
        return resource;
    }
}
