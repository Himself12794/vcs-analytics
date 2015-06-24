package com.cisco.dft.sda.api.config;

import java.io.IOException;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;


@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableConfigurationProperties( GraphiteConfigParams.class )
public class GraphiteConfigLoader {

	/**
	 * Initializes and reads configuration properties from application.yaml file
	 * and translates each properties into an object (e.g: GraphiteConfigParams)
	 * 
	 * @return property source containing the values
	 * @throws IOException if the file does not exist
	 */
	@Bean
	public PropertySource<?> yamlPropertySourceLoader() throws IOException {
		YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
		PropertySource<?> applicationYamlPropertySource = loader.load(
				"application.yml", new ClassPathResource("application.yml"),
				"default");
		return applicationYamlPropertySource;
	}
}