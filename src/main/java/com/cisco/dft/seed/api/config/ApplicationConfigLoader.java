package com.cisco.dft.seed.api.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * Initializes and loads application.yml file into a PropertySource (represents
 * a name & value property pairs)
 * 
 * @author sujmuthu
 * @version 1.0
 * @date February 16, 2015
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableConfigurationProperties({ ApplicationConfigParams.class, GraphiteConfigParams.class, JdbcConfigParams.class })
public class ApplicationConfigLoader {

	/**
	 * Initializes and reads configuration properties from application.yaml file
	 * and translates each properties into an object (e.g: ApplicationConfigParams)
	 * 
	 * @return
	 * @throws IOException
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
