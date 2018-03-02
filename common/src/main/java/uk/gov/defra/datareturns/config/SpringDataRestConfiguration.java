package uk.gov.defra.datareturns.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Data REST configuration
 *
 * @author Sam Gardner-Dell
 */
@Configuration
@ConditionalOnWebApplication
@Slf4j
@RequiredArgsConstructor
public class SpringDataRestConfiguration implements RepositoryRestConfigurer {
    /**
     * the JPA {@link EntityManagerFactory}
     */
    private final EntityManagerFactory factory;

    /**
     * Reference to the spring managed {@link Validator}
     */
    private final Validator validator;

    @Override
    public final void configureRepositoryRestConfiguration(final RepositoryRestConfiguration config) {
        // Scan for and configure projections with sub-type support
        final Reflections rf = new Reflections(SpringDataConfiguration.DATA_PACKAGE);
        final Set<Class<?>> projections = rf.getTypesAnnotatedWith(Projection.class);
        for (final Class<?> c : projections) {
            final Projection p = c.getAnnotation(Projection.class);

            // Add entity classes with projection annotation
            final Set<Class<?>> entityClasses = Arrays.stream(p.types())
                    .filter(t -> t.isAnnotationPresent(Entity.class))
                    .collect(Collectors.toSet());

            // Append any entity subtypes
            entityClasses.addAll(Arrays.stream(p.types())
                    .flatMap(e -> rf.getSubTypesOf(e).stream())
                    .filter(e -> e.isAnnotationPresent(Entity.class))
                    .collect(Collectors.toSet()));

            log.info("Registering projection {} from class {} with entity classes {}", p.name(), c.getSimpleName(), entityClasses);
            config.getProjectionConfiguration().addProjection(c, p.name(), entityClasses.toArray(new Class[entityClasses.size()]));
        }

        // Expose ids.
        final Metamodel metamodel = factory.getMetamodel();
        for (final ManagedType<?> managedType : metamodel.getManagedTypes()) {
            final Class<?> javaType = managedType.getJavaType();
            if (javaType.isAnnotationPresent(Entity.class)) {
                config.exposeIdsFor(managedType.getJavaType());
            }
        }
//        config.setRelProvider(new SpringDataConfiguration.SnakeCaseRelProvider());
//            config.useHalAsDefaultJsonMediaType(false);
    }

    @Override
    public final void configureConversionService(final ConfigurableConversionService conversionService) {

    }

    @Override
    public final void configureValidatingRepositoryEventListener(final ValidatingRepositoryEventListener validatingListener) {
        validatingListener.addValidator("beforeCreate", validator);
        validatingListener.addValidator("beforeSave", validator);
    }

    @Override
    public final void configureExceptionHandlerExceptionResolver(final ExceptionHandlerExceptionResolver exceptionResolver) {
    }

    @Override
    public final void configureHttpMessageConverters(final List<HttpMessageConverter<?>> messageConverters) {
    }

    @Override
    public final void configureJacksonObjectMapper(final ObjectMapper objectMapper) {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    /**
     * Configure the paging functionality to disable pagination of collection resources unless page and size parameters are supplied
     * on the request
     *
     * @param pageableResolver (injected) - the spring {@link HateoasPageableHandlerMethodArgumentResolver}
     * @return the spring {@link HateoasPageableHandlerMethodArgumentResolver}
     */
    @Bean
    public HateoasPageableHandlerMethodArgumentResolver customResolver(final HateoasPageableHandlerMethodArgumentResolver pageableResolver) {
        pageableResolver.setOneIndexedParameters(true);
        pageableResolver.setFallbackPageable(null);
        // TODO: Spring boot 2:
//        pageableResolver.setFallbackPageable(Pageable.unpaged());
        return pageableResolver;
    }
}
