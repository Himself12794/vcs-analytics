package com.cisco.dft.sda.api.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.dft.sda.api.pojo.GenericResponseObject;
import com.cisco.dft.sda.api.service.TestService;

/**
 * A generic controller responsible to handle HTTP requests.
 * 
 * @author sujmuthu
 * @version 1.0
 * @date January 22, 2015
 */
@RestController
public class Controller {

	static Logger LOGGER = LoggerFactory.getLogger(Controller.class); // defining
																		// the
																		// slf4j
																		// Logger
																		// Object

	@Autowired
	private TestService service;

	/**
	 * Test implementation
	 * 
	 * @param name
	 * @return returns the value of the path variable passed in.
	 */
	@RequestMapping(value = "/dft/sda/test/{name}", method = RequestMethod.GET)
	public String test(@PathVariable String name) {

		LOGGER.info("Input value:" + name); // Logs the name passed in the query
											// parameter with logger level info
		LOGGER.warn("Input value:" + name); // Logs the name passed in the query
											// parameter with logger level warn
    /* sample error logging scenario */
		try {
			int i = 1 / 0;
		} catch (Exception e) {
			LOGGER.error("division by 0", e);// catching the '1/0' exception
												// here with logger level error
												// with exception object e.
		}

		return service.test(name);
	}
	
	/**
	 * Set the http response body with a custom json instead of sending an object
	 * 
	 * @param response
	 * @return void
	 */
	@RequestMapping(value = "/dft/sda/testResponse", method = RequestMethod.GET)
	public void testHttpResponse(HttpServletResponse response) {
		
		try {
			// setting the type of the data which will be sent in http response body
			response.setContentType("application/json");
			
			// setting the status code of the http response header
			response.setStatus(200); // code can be any number or can be a predefined http status code
			
			// writing the data to the http response body @param : object
			response.getWriter().print(service.getObjectJSON());
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Status code defined here for easy debugging
	 * 
	 * @param response
	 * @return void
	 */
	@RequestMapping(value = "/dft/sda/testResponseObject", method = RequestMethod.GET)
	public GenericResponseObject testHttpResponseObject(HttpServletResponse response) {
		
		// setting the type of the data which will be sent in http response body
		response.setContentType("application/json");
		
		// setting the status code of the http response header
		response.setStatus(200); // code can be any number or can be a predefined http status code
		
		return service.getObject();
	}


}
