package com.cisco.dft.sda.api.service;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.cisco.dft.sda.api.pojo.GenericResponseObject;

/**
 * Implements API business logic
 * 
 * @author sujmuthu
 * @version 1.0
 * @date January 22, 2015
 */
@Service
public class TestService {

	/**
	 * Test implementation
	 * 
	 * @param name
	 * @return
	 */
	public String test(String name) {
		return name;
	}
	
	public JSONObject getObjectJSON() {
		// dummy
		JSONObject json = new JSONObject();
		json.put("param1", "hello");
		json.put("param2", "world");
		
		return json;
	}
	
	public GenericResponseObject getObject() {
		// dummy
		GenericResponseObject objGenericResponse = new GenericResponseObject();
		objGenericResponse.setParam1("hello");
		objGenericResponse.setParam2("world");
		
		return objGenericResponse;
	}
}
