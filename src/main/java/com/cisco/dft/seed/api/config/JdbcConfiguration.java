package com.cisco.dft.seed.api.config;

import javax.annotation.Resource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Represents the configuration for JDBC connection attributes
 *
 * @author sujmuthu
 * @version 1.0
 * @date June 24, 2015
 */
@Configuration
public class JdbcConfiguration {

	@Resource
	private JdbcConfigParams jdbcConfigParams;

	@Bean
	public BasicDataSource dataSource() throws Exception {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(jdbcConfigParams.getUrl());
		dataSource.setDriverClassName(jdbcConfigParams.getDriverClassName());
		dataSource.setUsername(jdbcConfigParams.getUsername());
		dataSource.setPassword(jdbcConfigParams.getPassword());
		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate() throws Exception {
		return new JdbcTemplate(dataSource());
	}
}