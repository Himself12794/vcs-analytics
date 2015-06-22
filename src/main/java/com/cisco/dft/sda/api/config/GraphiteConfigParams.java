package com.cisco.dft.sda.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "graphite", ignoreInvalidFields=true)
public class GraphiteConfigParams {
	
	public boolean enabled;
	
	public String prefix;
	
	public String host;

	public int port;
	
	public boolean getEnabled() { return enabled; }
	
	public void setEnabled(boolean status) {enabled = status;}
		
	public String getHost() { return host; }
	
	public void setHost(String host) { this.host = host; }

	public int getPort() { return port; }
	
	public void setPort(int port) { this.port = port; }
	
	public String getPrefix() { return prefix; }
	
	public void setPrefix(String key) { prefix = key; }
	
}