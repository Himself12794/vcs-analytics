package com.cisco.dft.sda.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implements API business logic
 * 
 * @author sujmuthu
 * @version 1.0
 * @date January 22, 2015
 */
@Service
public class TestService {
	
	// defining the slf4j Logger Object
	static Logger LOGGER = LoggerFactory.getLogger(TestService.class);
	
	/**
	 * Test implementation
	 * 
	 * @param name
	 * @return
	 */ 
	public String test(String name){
		return (name);
	}

	/**
	 * Test implementation for checking debugger
	 * 
	 * @param name
	 * @return
	 */ 
	
	public String loggerTest(String name) {
		// Logs the name passed in the query parameter with logger level debug
		LOGGER.debug("Logger Test (debug): Input value:" + name); 
		
		// Logs the name passed in the query parameter with logger level info
		LOGGER.info("Logger Test (info): Input value:" + name); 
		
		// Logs the name passed in the query parameter with logger level warn
		LOGGER.warn("Logger Test (warn): Input value:" + name); 
		
		/* sample error logging scenario */
		try {
			int i = 1 / 0;
		} catch (Exception e) {
			// catching the '1/0' exception here with logger level error with exception object e.
			LOGGER.error("division by 0", e);
		}
		return name;
	}
}
