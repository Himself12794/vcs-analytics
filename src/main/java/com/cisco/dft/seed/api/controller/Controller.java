package com.cisco.dft.seed.api.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.cisco.dft.seed.api.pojo.GenericRequestObject;
import com.cisco.dft.seed.api.pojo.GenericResponseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.dft.seed.api.service.TestService;
import com.codahale.metrics.annotation.Timed;

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
	//Use "@Timed" annotation as shown here in order to generate metrics for this API
	@Timed(name = "test", absolute = true)
	@RequestMapping(value = "/dft/seed/test/{name}", method = RequestMethod.GET)
	public String test(@PathVariable String name) {
		LOGGER.info("test() received params: " + "name: " + name);
		return name;
	}

	/**
	 * Test implementation using HTTP GET Usage:
	 * <server-name>:<port-num>/dft/seed/test?param1=Hello&param2=Hello
	 * 
	 * @param param1
     * @param param2
	 * @return
	 */
    //Use "@Timed" annotation as shown here in order to generate metrics for this API
	@Timed(name = "testGet", absolute = true)
	@RequestMapping(value = "/dft/seed/test", method = RequestMethod.GET)
	public GenericResponseObject testGet(
			@RequestParam(value = "param1", defaultValue = "param1", required = true) String param1,
			@RequestParam(value = "param2", defaultValue = "param2") String param2,
			HttpServletResponse httpResponse) {
		LOGGER.info("testGet() i/p: " + "param1: " + param1 + ", " + "param2: " + param2);
		GenericRequestObject request = new GenericRequestObject(param1, param2);
        GenericResponseObject response = service.test(request, httpResponse);
        LOGGER.info("testGet() status: " + httpResponse.getStatus());
        LOGGER.info("testGet() o/p: " + response.toString());
		return response;

		// send a custom json instead:
		// try {
		// httpResponse.getWriter().print(service.getObjectJSON());
		// } catch (IOException e) {
		// LOGGER.error(e.getStackTrace().toString());
		// }
	}

	/**
	 * Test implementation using HTTP POST exchange Sample Payload: {"param1":
	 * "Hello", "param2": "Hello"}
	 * 
	 * @param request
	 * @return
	 */
    //Use "@Timed" annotation as shown here in order to generate metrics for this API
	@Timed(name = "testPost", absolute = true)
	@RequestMapping(value = "/dft/seed/test", method = RequestMethod.POST)
	public GenericResponseObject testPost(
			@RequestBody @Valid final GenericRequestObject request,
			HttpServletResponse httpResponse) {
        LOGGER.info("testPost() i/p: " + "RequestBody: " + request.toString());
        GenericResponseObject response = service.test(request, httpResponse);
        LOGGER.info("testPost() status: " + httpResponse.getStatus());
        LOGGER.info("testPost() o/p: " + response.toString());
		return response;

		// send a custom json instead:
		// try {
		// httpResponse.getWriter().print(service.getObjectJSON());
		// } catch (IOException e) {
		// LOGGER.error(e.getStackTrace().toString());
		// }
	}

}