package com.cisco.dft.sda.api.config;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;

@Configuration
@EnableMetrics
public class MetricReportingConfiguration extends MetricsConfigurerAdapter {
	
	@Autowired
	private Graphite graphite;
	
	@Resource
	private GraphiteConfigParams config;
	
    @Override
    public void configureReporters(MetricRegistry metricRegistry) {
        

    	
        if ( config.enabled ){
        	
        	ConsoleReporter
            	.forRegistry(metricRegistry)
            	.build()
            	.start(10, TimeUnit.SECONDS);
        	
        	GraphiteReporter
	        	.forRegistry( metricRegistry )
	        	.prefixedWith( config.getPrefix() )
	            .convertRatesTo(TimeUnit.SECONDS)
	            .convertDurationsTo(TimeUnit.MILLISECONDS)
	            .filter(MetricFilter.ALL)
	        	.build( graphite )
	        	.start( 10, TimeUnit.SECONDS );
        }
    }
   
}