package uk.gov.defra.datareturns.config;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.JvmAttributeGaugeSet;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
public class DropWizardMetricsConfiguration extends MetricsConfigurerAdapter {
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

        // Report metrics over JMX
        registerReporter(JmxReporter.forRegistry(metricRegistry).build()).start();

        // Console debug reporting
        registerReporter(Slf4jReporter.forRegistry(metricRegistry)
                .withLoggingLevel(Slf4jReporter.LoggingLevel.DEBUG).build())
                .start(120, TimeUnit.SECONDS);
    }
}
