package uk.gov.defra.datareturns.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.annotations.Indexed;
import org.reflections.ReflectionUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.stereotype.Service;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Service to provide access to spring data repositories for various lookup types (such as a URL path or entity class)
 *
 * @author Sam Gardner-Dell
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RepositoryLookupService {
    /**
     * Spring data rest {@link org.springframework.data.rest.core.mapping.ResourceMapping} configuration
     */
    private final ResourceMappings resourceMappings;
    /**
     * Spring {@link ListableBeanFactory}
     */
    private final ListableBeanFactory listableBeanFactory;

    /**
     * Map of repositories by the entity class they operate on
     */
    private final Map<Class<?>, BaseRepository<? extends AbstractBaseEntity, ?>> repositoryMap = new HashMap<>();
    /**
     * Map of repositories by the path exposed by the restful API
     */
    private final Map<String, Class<?>> domainTypesByPath = new HashMap<>();
    /**
     * List of searchable fields for a given entity class
     */
    private final Map<Class<?>, String[]> searchFieldsByDomainType = new LinkedHashMap<>();

    /**
     * Build the mapping data to support the lookup methods
     */
    @PostConstruct
    public void buildMappings() {
        // Build map of REST paths to their respective domain types
        for (final ResourceMetadata metadata : resourceMappings) {
            if (metadata.isExported()) {
                domainTypesByPath.put(metadata.getPath().toString().substring(1), metadata.getDomainType());
            }
        }
        // Build repository map from domain type
        final Repositories repositories = new Repositories(listableBeanFactory);
        for (final Class<?> domainType : domainTypesByPath.values()) {
            // Map repository for domain type
            this.repositoryMap.put(domainType, (BaseRepository<? extends AbstractBaseEntity, ?>) repositories.getRepositoryFor(domainType));

            // Find searchable fields
            if (domainType.isAnnotationPresent(Indexed.class)) {
                final Predicate<? super Field> predicate = (f) -> f != null && f.isAnnotationPresent(org.hibernate.search.annotations
                        .Field.class);
                @SuppressWarnings("unchecked") final Set<Field> fieldSet = ReflectionUtils.getAllFields(domainType, predicate::test);
                final String[] fields = fieldSet.stream().map(Field::getName).toArray(String[]::new);
                searchFieldsByDomainType.put(domainType, fields);
            }
        }
    }

    /**
     * Retrieve a repository, implementing {@link BaseRepository}, for the given domain entity class
     *
     * @param domainClass the domain entity class the returned repository will operate on
     * @return the repository for the given domain entity class or null if not found
     */
    @SuppressWarnings("unchecked")
    public BaseRepository<? extends AbstractBaseEntity, ?> getRepository(final Class<?> domainClass) {
        return repositoryMap.get(domainClass);
    }

    /**
     * Retrieve a domain type for the given RESTful service path variable
     *
     * @param pathVariable the path variable for a given entity as mapped by Spring Data REST
     * @return the repository for the given domain entity class or null if not found
     */
    public Class<?> getDomainType(final String pathVariable) {
        return domainTypesByPath.get(pathVariable);
    }


    /**
     * Retrieve the searchable fields for the given domain class
     *
     * @param domainType the class of the target domain entity
     * @return the names of all searchable fields as an array of {@link String}
     */
    public String[] getSearchableFields(final Class<?> domainType) {
        return searchFieldsByDomainType.get(domainType);
    }
}
