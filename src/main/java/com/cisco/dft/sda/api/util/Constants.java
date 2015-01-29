package com.cisco.dft.sda.api.util;

/**
 * Holds all constants. Make sure the constant name in all upper case letters
 * with underscores for spaces.
 * 
 * @author sujmuthu
 * @version 1.0
 * @date January 22, 2015
 */
public interface Constants {

	enum STATUS {
		SUCCESS, FAILURE, INVALID_REQUEST;
	}

	public static final String SOME_NAME = "some value";
	public static final String QUEUE_NAME = "test";
	public static final String TOPIC_EXCHANGE_NAME = "spring-boot-exchange";
	public static final String USERNAME = "indrayam";
	public static final String PASSWORD = "raju2she";
	public static final int PORT = 5672;
	public static final String VIRTUALHOST = "dft-dev-vhost";
	public static final String HOSTNAME = "indrayam2.cisco.com";
}
