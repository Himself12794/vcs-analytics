package com.cisco.dft.sda.api.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines helper/util methods that performs common functionalities which
 * doesn't depend on the state of the object.
 * 
 * @author sujmuthu
 * @version 1.0
 * @date January 22, 2015
 */
public class Util {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Util.class);

	/**
	 * Determines whether the given string is a null or an empty string
	 * 
	 * @param value
	 * @return
	 */
	public static Boolean isNullOrEmpty(String value) {
		return (value == null || value.trim().length() == 0);
	}
	
	/**
	 * Decrypts the password using the Jasypt library 
	 * @param encrypted password, key
	 *            
	 * @return decrypted password
	 */
	public static String decrypt(String password, String key) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		String encryptedPassword = null;
		encryptor.setPassword(key);
		try {
			LOGGER.info("Decrypting the password:  " + password);
			encryptedPassword = encryptor.decrypt(password);
			LOGGER.info("Encrypted Password:  " + encryptedPassword);
		} catch (Exception e) {

			LOGGER.error("Error occurred while decrypting password", e);
		}
		return encryptedPassword;
	}
}
