package com.cisco.dft.sda.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Defines the Graphite reporting attributes (with prefix "graphite") specified in the
 * application.yml configuration file.
 * 
 * @author phwhitin
 * @version 1.0
 * @since June 23, 2015
 */
@ConfigurationProperties(prefix = "graphite", ignoreInvalidFields=true)
public class GraphiteConfigParams {
	
	public boolean graphiteReportingEnabled;
	
	public boolean consoleReportingEnabled;

	public int reportRate;
	
	public String prefix;
	
	public String host;

	public int port;

    public boolean isGraphiteReportingEnabled() {
        return graphiteReportingEnabled;
    }

    public void setGraphiteReportingEnabled(boolean graphiteReportingEnabled) {
        this.graphiteReportingEnabled = graphiteReportingEnabled;
    }

    public int getReportRate() { return reportRate; }

	public void setReportRate(int reportRate) { this.reportRate = reportRate; }
	
	public boolean isConsoleReportingEnabled() { return this.consoleReportingEnabled; } 
	
	public void setConsoleReportingEnabled(boolean status) { this.consoleReportingEnabled = status; }
		
	public String getHost() { return host; }
	
	public void setHost(String host) { this.host = host; }

	public int getPort() { return port; }
	
	public void setPort(int port) { this.port = port; }
	
	public String getPrefix() { return prefix; }
	
	public void setPrefix(String key) { prefix = key; }
	
}