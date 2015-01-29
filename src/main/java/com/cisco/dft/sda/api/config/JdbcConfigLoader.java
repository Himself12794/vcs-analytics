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

/**
 * Initializes and loads application.yml file into a PropertySource (represents
 * a name & value property pairs)
 * 
 * @author sujmuthu
 * @version 1.0
 * @date January 22, 2015
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableConfigurationProperties(JdbcConfigParams.class)
public class JdbcConfigLoader {

	/**
	 * This bean is responsible to initialize and read the contents of the
	 * application.yml file loaded from the class path and translates each
	 * attributes to a Java Object (e.g: JdbcConfigParams) based on the
	 * specified active profile (dev or prod)
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
