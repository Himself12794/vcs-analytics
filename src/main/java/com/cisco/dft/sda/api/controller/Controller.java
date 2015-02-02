package com.cisco.dft.sda.api.controller;

import org.apache.log4j.*;
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

	 static  Logger LOGGER = Logger
			.getLogger(Controller.class);

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
		LOGGER.info("Input value:" + name);
		return service.test(name);
	}

}
