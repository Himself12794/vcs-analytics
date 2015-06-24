package com.cisco.dft.seed.api.config;

import java.net.InetSocketAddress;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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

		String host = config.getHost() == null ? "localhost" : config.getHost();
		int port = config.getPort();
		return new Graphite(new InetSocketAddress(host, port));
	}
	
}
