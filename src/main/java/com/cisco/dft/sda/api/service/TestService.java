package com.cisco.dft.sda.api.service;

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

	/**
	 * Test implementation
	 * 
	 * @param name
	 * @return
	 */
	public String test(String name) {
		return name;
	}
}
