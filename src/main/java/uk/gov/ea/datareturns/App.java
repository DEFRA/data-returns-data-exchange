package uk.gov.ea.datareturns;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import uk.gov.ea.datareturns.config.DataExchangeConfiguration;
import uk.gov.ea.datareturns.config.storage.LocalStorageSettings;
import uk.gov.ea.datareturns.config.storage.StorageSettings;
import uk.gov.ea.datareturns.health.DatabaseHealthCheck;
import uk.gov.ea.datareturns.health.StorageHealthCheck;
import uk.gov.ea.datareturns.jpa.dao.AbstractJpaDao;
import uk.gov.ea.datareturns.resource.DataExchangeResource;
import uk.gov.ea.datareturns.storage.StorageProvider;
import uk.gov.ea.datareturns.storage.local.LocalStorageProvider;
import uk.gov.ea.datareturns.storage.s3.AmazonS3StorageProvider;

// TODO javadoc
public class App extends Application<DataExchangeConfiguration> {

	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	public static void main(final String[] args) throws Exception {
		new App().run(args);
	}

	@Override
	public void initialize(final Bootstrap<DataExchangeConfiguration> bootstrap) {
	}

	@Override
	public void run(final DataExchangeConfiguration config, final Environment environment) {
		configureCors(environment);

		final StorageProvider storageProvider = initialiseStorageProvider(config);

		initialiseJPA(config);

		environment.jersey().register(new DataExchangeResource(config, storageProvider));
		environment.jersey().register(new MultiPartFeature());

		// Useful to log JSON responses from the API
		if (config.getMiscSettings().isDebugMode()) {
			environment.jersey().register(new LoggingFilter(java.util.logging.Logger.getLogger(LoggingFilter.class.getName()), true));
		}

		// Initialize the health checks
		initializeHealthChecks(environment, config, storageProvider);
	}

	private void initializeHealthChecks(Environment environment, DataExchangeConfiguration config, StorageProvider storageProvider) {
		final DatabaseHealthCheck databaseHealthCheck = new DatabaseHealthCheck(config);
		environment.healthChecks().register("Database Health Check", databaseHealthCheck);

		final StorageHealthCheck storageHealthCheck = new StorageHealthCheck(storageProvider);
		environment.healthChecks().register("Storage Health Check", storageHealthCheck);
	}

	private static void initialiseJPA(final DataExchangeConfiguration config) {
		final Map<String, String> persistenceProps = new HashMap<>();
		persistenceProps.put("hibernate.connection.url", config.getDatabase().getUrl());
		persistenceProps.put("hibernate.connection.driver_class", config.getDatabase().getDriverClass());
		persistenceProps.put("hibernate.connection.username", config.getDatabase().getUser());
		persistenceProps.put("hibernate.connection.password", config.getDatabase().getPassword());
		persistenceProps.put("hibernate.dialect", config.getDatabase().getDialect());
		AbstractJpaDao.configure(persistenceProps);
	}

	/**
	 * Configures the storage provider based on the application configuration settings.
	 *
	 * @param config the {@link DataExchangeConfiguration} from which configurations settings are read
	 * @return an instance of {@link StorageProvider} based on the application settings.
	 */
	private static StorageProvider initialiseStorageProvider(final DataExchangeConfiguration config) {
		final StorageSettings storeCfg = config.getStorageSettings();
		StorageProvider provider = null;
		switch (storeCfg.getStorageType()) {
			case LOCAL:
				final LocalStorageSettings localCfg = storeCfg.getLocalConfig();
				final File tempDir = new File(localCfg.getTemporaryFolder());
				final File persistDir = new File(localCfg.getPersistentFolder());

				if (localCfg.isCleanOnStartup()) {
					try {
						for (final File dir : new File[] { persistDir, tempDir }) {
							FileUtils.forceMkdir(dir);
							FileUtils.cleanDirectory(dir);
						}
					} catch (final IOException e) {
						LOGGER.error("Unable to clear local storage directories", e);
					}
				}

				provider = new LocalStorageProvider(tempDir, persistDir);
				break;
			case S3:
				provider = new AmazonS3StorageProvider(storeCfg.getS3Config());
				break;
			default:
				throw new RuntimeException("No storage provider has been configured");
		}
		return provider;
	}

	// TODO CORS config for release?
	private static void configureCors(final Environment env) {
		final FilterRegistration.Dynamic filter = env.servlets().addFilter("CORS", CrossOriginFilter.class);
		filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
		filter.setInitParameter("allowCredentials", "true");
	}
}
