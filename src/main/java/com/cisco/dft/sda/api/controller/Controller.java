package com.cisco.dft.sda.api.controller;

// importing the slf4j jar
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

}
