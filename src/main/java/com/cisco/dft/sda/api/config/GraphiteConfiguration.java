package com.cisco.dft.sda.api.config;

import java.net.InetSocketAddress;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.cisco.dft.sda.api.config.GraphiteConfigParams;
import com.codahale.metrics.graphite.Graphite;

/**
 * Configures the Graphite server connection and stores it as a bean. 
 * 
 * @author phwhitin
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class GraphiteConfiguration {

	@Resource
	private GraphiteConfigParams config;
	
	@Bean
	public Graphite getGraphiteServer() {
		return new Graphite(new InetSocketAddress(config.getHost(), config.getPort()));
	}
	
}
