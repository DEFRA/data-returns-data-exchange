package uk.gov.ea.datareturns.config;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Stores database connection settings
 *
 * @author Sam Gardner-Dell
 */
@Configuration
@EnableTransactionManagement
public class PersistenceConfiguration {

    /**
     * Creates the primary application datasource from the spring.datasource section of the configuration
     *
     * @return the application's primary {@link DataSource}
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Create the Hibernate JPA adaptor
     * @return
     */
    private JpaVendorAdapter vendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        return vendorAdapter;
    }

    /**
     * Bean record for the JPA entity manager
     *
     * @return a {@link LocalContainerEntityManagerFactoryBean} configured for the application database
     */
    private LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactory.setJpaVendorAdapter(vendorAdapter());
        entityManagerFactory.setDataSource(primaryDataSource());
        entityManagerFactory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactory.setPersistenceUnitName("uk.gov.ea.datareturns.domain.jpa");
        entityManagerFactory.setPackagesToScan("uk.gov.ea.datareturns.domain.jpa.entities");
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter());
        return entityManagerFactory;
    }

    /**
     * Declare the transaction manager.
     *
     * @return the {@link JpaTransactionManager}
     */
    @Bean
    public JpaTransactionManager transactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    /**
     *
     * PersistenceExceptionTranslationPostProcessor is a bean post processor which adds an advisor to any bean annotated
     * with Repository so that any platform-specific exceptions are caught and then rethrown as one Spring's unchecked data
     * access exceptions (i.e. a subclass of DataAccessException).
     *
     * @return the {@link PersistenceExceptionTranslationPostProcessor}
     */
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}