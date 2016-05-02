/**
 *
 */
package uk.gov.ea.datareturns.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Stores database connection settings
 *
 * @author Sam Gardner-Dell
 *
 */
@Configuration
@EnableTransactionManagement
public class DatabaseConfiguration {
	
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

	@Bean
	@ConditionalOnMissingBean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setDataSource(primaryDataSource());
		entityManagerFactory.setPersistenceUnitName("org.gov.ea.datareturns.jpa");
//		entityManagerFactory.setPackagesToScan("uk.gov.ea.datareturns.jpa.entities");

		// Vendor adapter
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		entityManagerFactory.setJpaVendorAdapter(vendorAdapter);

		// Additional hibernate properties
		//Properties additionalProperties = new Properties();
		//entityManagerFactory.setJpaProperties(additionalProperties);
		return entityManagerFactory;
	}

	/**
	 * Declare the transaction manager.
	 */
	@Bean
	public JpaTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		return transactionManager;
	}

	/**
	 * 
	 * PersistenceExceptionTranslationPostProcessor is a bean post processor which adds an advisor to any bean annotated 
	 * with Repository so that any platform-specific exceptions are caught and then rethrown as one Spring's unchecked data 
	 * access exceptions (i.e. a subclass of DataAccessException).
	 * 
	 * @return
	 */
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}
}