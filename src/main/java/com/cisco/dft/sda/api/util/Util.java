package com.cisco.dft.sda.api.util;

/**
 * Defines helper/util methods that performs common functionalities which
 * doesn't depend on the state of the object.
 * 
 * @author sujmuthu
 * @version 1.0
 * @date January 22, 2015
 */
public class Util {

	/**
	 * Determines whether the given string is a null or an empty string
	 * 
	 * @param value
	 * @return
	 */
	public static Boolean isNullOrEmpty(String value) {
		return (value == null || value.trim().length() == 0);
	}
}
