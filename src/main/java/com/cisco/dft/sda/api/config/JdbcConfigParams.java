package com.cisco.dft.sda.api.config;

import com.cisco.dft.sda.api.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Defines the JDBC attributes (with prefix "datasource") specified in the
 * application.yml configuration file.
 * 
 * @author sujmuthu
 * @version 1.0
 * @date January 22, 2015
 */
@Component
@ConfigurationProperties(prefix = "datasource")
public class JdbcConfigParams {

	@Autowired
	private ApplicationConfigParams applicationConfigParams;

	private String driverClassName;
	private String url;
	private String username;
	private String password;

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@PostConstruct
	public void decrypt (){
		this.password=Util.decrypt(this.password, applicationConfigParams.getDecryptionKey());
		
	}
}
