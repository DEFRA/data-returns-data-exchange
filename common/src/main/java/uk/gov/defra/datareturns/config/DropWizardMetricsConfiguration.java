package uk.gov.defra.datareturns.config;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.JvmAttributeGaugeSet;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.jetty9.InstrumentedHandler;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import org.eclipse.jetty.server.Server;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for the metrics exposed over the administration port
 *
 * @author Sam Gardner-Dell
 */
@Configuration
@EnableMetrics
@ConfigurationProperties(prefix = "metrics")
public class DropWizardMetricsConfiguration extends MetricsConfigurerAdapter implements JettyServerCustomizer {
    private InstrumentedHandler metricsHandler;

    /**
     * Configure the available metrics reporters
     *
     * @param metricRegistry the metric registry upon which reporters are configured.
     */
    @Override
    public void configureReporters(final MetricRegistry metricRegistry) {
        metricRegistry.register("jvm.attribute", new JvmAttributeGaugeSet());
        metricRegistry.register("jvm.classloader", new ClassLoadingGaugeSet());
        metricRegistry.register("jvm.filedescriptor", new FileDescriptorRatioGauge());
        metricRegistry.register("jvm.gc", new GarbageCollectorMetricSet());
        metricRegistry.register("jvm.memory", new MemoryUsageGaugeSet());
        metricRegistry.register("jvm.threads", new ThreadStatesGaugeSet());
        this.metricsHandler = new InstrumentedHandler(metricRegistry, "jetty");

        // Report metrics over JMX
        registerReporter(JmxReporter.forRegistry(metricRegistry).build()).start();

        // Console debug reporting
        registerReporter(Slf4jReporter.forRegistry(metricRegistry)
                .withLoggingLevel(Slf4jReporter.LoggingLevel.DEBUG).build())
                .start(120, TimeUnit.SECONDS);
    }

    /**
     * Bean factory for the {@link EmbeddedServletContainerCustomizer} which allows us to customise the
     * servlet container
     *
     * @return the EmbeddedServletContainerCustomizer
     */
    @Bean
    public EmbeddedServletContainerCustomizer customizer() {
        return container -> {
            if (container instanceof JettyEmbeddedServletContainerFactory) {
                ((JettyEmbeddedServletContainerFactory) container).addServerCustomizers(DropWizardMetricsConfiguration.this);
            }
        };
    }

    /**
     * Customisation hook for the Jetty server.
     * Allows us to hook up the metrics system to Jetty
     *
     * @param server the Jetty {@link Server} instance
     */
    @Override
    public void customize(final Server server) {
        this.metricsHandler.setHandler(server.getHandler());
        server.setHandler(this.metricsHandler);
    }
}
