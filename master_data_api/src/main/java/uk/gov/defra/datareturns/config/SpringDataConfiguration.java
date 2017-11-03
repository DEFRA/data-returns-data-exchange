package uk.gov.defra.datareturns.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import uk.gov.defra.datareturns.data.model.BaseRepository;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import java.util.List;

/**
 * Configuration for Spring JPA Repositories
 *
 * @author Sam Gardner-Dell
 */
@Configuration
@EnableTransactionManagement
@EntityScan(SpringDataConfiguration.DATA_PACKAGE)
@EnableJpaAuditing
@EnableJpaRepositories(
        basePackages = SpringDataConfiguration.DATA_PACKAGE,
        considerNestedRepositories = true,
        repositoryBaseClass = BaseRepository.BaseRepositoryImpl.class
)
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class SpringDataConfiguration {
    public static final String DATA_PACKAGE = "uk.gov.defra.datareturns.data.model";


    /**
     * Spring Data REST configuration
     *
     * @author Sam Gardner-Dell
     */
    @Configuration
    @ConditionalOnWebApplication
    @Import(SwaggerConfig.class)
    @Slf4j
    @RequiredArgsConstructor
    public static class SpringDataRestConfiguration implements RepositoryRestConfigurer {
        /**
         * the JPA {@link javax.persistence.EntityManagerFactory}
         */
        private final EntityManagerFactory factory;

        /**
         * Reference to the spring managed {@link org.springframework.validation.Validator}
         */
        private final Validator validator;

        @Override
        public final void configureRepositoryRestConfiguration(final RepositoryRestConfiguration config) {
            final Metamodel metamodel = factory.getMetamodel();
            for (final ManagedType<?> managedType : metamodel.getManagedTypes()) {
                final Class<?> javaType = managedType.getJavaType();
                if (javaType.isAnnotationPresent(Entity.class)) {
                    config.exposeIdsFor(managedType.getJavaType());
                }
            }
            config.useHalAsDefaultJsonMediaType(false);
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
            return pageableResolver;
        }
    }

}
