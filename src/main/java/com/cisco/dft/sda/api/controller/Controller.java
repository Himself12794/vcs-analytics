package com.cisco.dft.sda.api.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.dft.sda.api.pojo.GenericResponseObject;
import com.cisco.dft.sda.api.pojo.GenericRequestObject;
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

	// defining the slf4j Logger Object
	static Logger LOGGER = LoggerFactory.getLogger(Controller.class); 

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
		return service.loggerTest(name);
	}

	/**
	 * Test implementation using HTTP GET Usage:
	 * <server-name>:<port-num>/dft/sda/get-test?param1=Hello&param2=World
	 * 
	 * @param param1
     * @param param2
	 * @return
	 */
	@RequestMapping(value = "/dft/sda/get-test", method = RequestMethod.GET)
	public GenericResponseObject testGet(
			@RequestParam(value = "param1", defaultValue = "param1", required = true) String param1,
			@RequestParam(value = "param2", defaultValue = "param2") String param2,
			HttpServletResponse httpResponse) {
		GenericRequestObject request = new GenericRequestObject(param1, param2);
		return service.test(request, httpResponse);

		// send a custom json instead:
		// try {
		// httpResponse.getWriter().print(service.getObjectJSON());
		// } catch (IOException e) {
		// LOGGER.error(e.getStackTrace().toString());
		// }
	}

	/**
	 * Test implementation using HTTP POST exchange Sample Payload: {"param1":
	 * "Hello", "param2": "World!"}
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/dft/sda/post-test", method = RequestMethod.POST)
	public GenericResponseObject testPost(
			@RequestBody @Valid final GenericRequestObject request,
			HttpServletResponse httpResponse) {
		return service.test(request, httpResponse);

		// send a custom json instead:
		// try {
		// httpResponse.getWriter().print(service.getObjectJSON());
		// } catch (IOException e) {
		// LOGGER.error(e.getStackTrace().toString());
		// }
	}

}
