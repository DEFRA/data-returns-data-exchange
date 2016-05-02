package uk.gov.ea.datareturns.config;

import org.eclipse.jetty.server.Server;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.JvmAttributeGaugeSet;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jetty9.InstrumentedHandler;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;

@Configuration
@EnableMetrics
public class MetricsConfiguration extends MetricsConfigurerAdapter implements JettyServerCustomizer {
	private InstrumentedHandler metricsHandler;

	@Override
	public void configureReporters(MetricRegistry metricRegistry) {
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

	@Bean
	public EmbeddedServletContainerCustomizer customizer() {
		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				if (container instanceof JettyEmbeddedServletContainerFactory) {
					((JettyEmbeddedServletContainerFactory) container).addServerCustomizers(MetricsConfiguration.this);
				}
			}
		};
	}

	@Override
	public void customize(Server server) {
		metricsHandler.setHandler(server.getHandler());
		server.setHandler(metricsHandler);
	}
}