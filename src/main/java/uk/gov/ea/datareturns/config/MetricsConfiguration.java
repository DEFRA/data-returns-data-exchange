package uk.gov.ea.datareturns.config;

import com.codahale.metrics.JvmAttributeGaugeSet;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jetty9.InstrumentedHandler;
import com.codahale.metrics.jvm.*;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import org.eclipse.jetty.server.Server;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the metrics exposed over the administration port
 *
 * @author Sam Gardner-Dell
 */
@Configuration
@EnableMetrics
public class MetricsConfiguration extends MetricsConfigurerAdapter implements JettyServerCustomizer {
    private InstrumentedHandler metricsHandler;

    /**
     * Configure the available metrics reporters
     *
     * @param metricRegistry the metric registry upon which reporters are configured.
     */
    @Override
    public void configureReporters(final MetricRegistry metricRegistry) {
        //    	Console reporter
        //        registerReporter(ConsoleReporter.forRegistry(metricRegistry).build()).start(1, TimeUnit.MINUTES);

        //		Graphite graphite = new Graphite(graphiteHost, 1337);
        //		GraphiteReporter graphiteReporter = GraphiteReporter.forRegistry(metricRegistry).build(graphite);
        //		registerReporter(graphiteReporter);
        //		graphiteReporter.start(10, TimeUnit.SECONDS);
        //
        metricRegistry.register("jvm.attribute", new JvmAttributeGaugeSet());
        metricRegistry.register("jvm.classloader", new ClassLoadingGaugeSet());
        metricRegistry.register("jvm.filedescriptor", new FileDescriptorRatioGauge());
        metricRegistry.register("jvm.gc", new GarbageCollectorMetricSet());
        metricRegistry.register("jvm.memory", new MemoryUsageGaugeSet());
        metricRegistry.register("jvm.threads", new ThreadStatesGaugeSet());
        this.metricsHandler = new InstrumentedHandler(metricRegistry, "jetty");

        //    JmxReporter.forRegistry(metricRegistry()).build().start();
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
                ((JettyEmbeddedServletContainerFactory) container).addServerCustomizers(MetricsConfiguration.this);
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