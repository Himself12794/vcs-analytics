package com.cisco.dft.seed.api.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.cisco.dft.seed.api.config.GraphiteConfigParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;

/**
 * This configures the metric reporting services. It can be configured to report
 * to the console, and/or the Graphite server.
 * 
 * @author phwhitin
 * @since June 23, 2015
 *
 */
@Configuration
@EnableMetrics
public class GraphiteMetricReportingService extends MetricsConfigurerAdapter {

	@Autowired
	private Graphite graphite;

	@Resource
	private GraphiteConfigParams config;

	/**
	 * Configures the separate reporters based on the application.yml.
	 */
	@Override
	public void configureReporters(MetricRegistry metricRegistry) {

		if (config.isGraphiteReportingEnabled()) {

			GraphiteReporter.forRegistry(metricRegistry)
					.prefixedWith(config.getPrefix())
					.convertRatesTo(TimeUnit.SECONDS)
					.convertDurationsTo(TimeUnit.MILLISECONDS)
					.filter(MetricFilter.ALL).build(graphite)
					.start(config.getReportRate(), TimeUnit.SECONDS);

		}

		if (config.isConsoleReportingEnabled()) {

			ConsoleReporter.forRegistry(metricRegistry)
					.convertRatesTo(TimeUnit.SECONDS)
					.convertDurationsTo(TimeUnit.MILLISECONDS)
					.filter(MetricFilter.ALL).build()
					.start(config.getReportRate(), TimeUnit.SECONDS);

		}
	}

}