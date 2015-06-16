package com.cisco.dft.sda.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.cisco.dft.sda.api.pojo.GenericResponseObject;
import com.cisco.dft.sda.api.pojo.GenericRequestObject;

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

	/**
	 * an over-ridden test method with custom business logic which returns a
	 * generic response object when called
	 * 
	 * @param request
	 * @param httpResponse
	 * @return
	 */

	public GenericResponseObject test(GenericRequestObject request,
			HttpServletResponse httpResponse) {
		GenericResponseObject response = new GenericResponseObject();
		String param1 = request.getParam1();
		String param2 = request.getParam2();

		// Business logic:
		if (param1.equals(param2)) {
			response.setParam1("value#1");
			response.setParam2("value#2");
			httpResponse.setContentType("application/json");
			httpResponse.setStatus(200); // status 200 will be returned
		} else {
			response.setParam1("value#3");
			response.setParam2("value#4");
			httpResponse.setContentType("application/json");
			httpResponse.setStatus(400); // status 400 will be returned
		}
		return response;
	}

	/**
	 * dummy method which creates and return a json
	 * @return
	 */

	public JSONObject getObjectJSON() {
		// dummy
		JSONObject json = new JSONObject();
		json.put("param1", "hello");
		json.put("param2", "world");

		return json;
	}
}
