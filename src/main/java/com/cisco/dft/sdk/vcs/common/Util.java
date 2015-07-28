package com.cisco.dft.sdk.vcs.common;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

/**
 * Utilities for use throughout the rest of the Application
 * 
 * @author phwhitin
 *
 */
public final class Util {

	private static final Logger LOGGER = LoggerFactory.getLogger("Utilities");

	private Util() {}
	
	/**
	 * Enables debug logging
	 */
	public static void setLoggingLevel(Level level) {
		LOGGER.info("Enabling " + level.toString() + " logging");
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	    root.setLevel(level);
	}
	
	/**
	 * When you're too lazy to create a logger for each class.
	 * 
	 * @param msg
	 * @param t
	 */
	public static void redirectLogError(String msg, Throwable t) {
		LOGGER.error(msg, t);
	}

	/**
	 * If the value does not exist for the key, it places it in the map.
	 * 
	 * @param map
	 * @param key
	 * @param value
	 * @return the value "value", mutates parameter "map"
	 */
	public static <K, V> V putIfAbsent(Map<K, V> map, K key, V value) {

		if (map.containsKey(key)) {
			return map.get(key);
		} else {
			map.put(key, value);
			return value;
		}

	}
	
	/**
	 * Similar to {@link Util#putIfAbsent(Map, Object, Object)}, except it does
	 * not place the default value into the map, only returns it.
	 * 
	 * @param map
	 * @param key
	 * @param defaultV
	 * @return the pre-existing value, or default if non-existent
	 */
	public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultV) {
		
		if (map.containsKey(key)) {
			return map.get(key);
		} else {
			return defaultV;
		}
		
	}
	
	/**
	 * If {@code arg0} is null, returns {@code arg0}, otherwise
	 * returns {@code arg0}.
	 * 
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public static <T> T ifNullDefault(T arg0, T arg1) {
		if (arg0 == null) { return arg1; }
		else { return arg0; } 
	}
	
	private static final int DEFAULT_ALLOWED_NAME_SIZE = 20;
	
	/**
	 * {@link Util#valueWithSpaces(Object, int)} with size set to 20.
	 * 
	 * @param x
	 * @return
	 */
	public static String valueWithSpaces(final Object x) {
		return valueWithFiller(x, DEFAULT_ALLOWED_NAME_SIZE, ' ');
	}
	
	/**
	 * Gets the string form of the obj, ensuring it is no longer than the given size,
	 * and if it is less, fills the rest of with spaces.
	 * 
	 * @param obj
	 * @param size
	 * @return
	 */
	public static String valueWithFiller(final Object obj, final int size, final char filler) {
		
		final String temp = String.valueOf(obj);
		final int length = temp.length();
		final String name = length > size ? temp.substring(0, size) : temp;
		
		final StringBuilder value = new StringBuilder();
		
		value.append(name);
		
		if (length < size) {
			
			for (int i = 0; i < size - length; i++) {
				
				value.append(filler);
				
			}
			
		}
		
		return value.toString();
	}
	
	public static String printNTimes(final char value, final int n) {
		return valueWithFiller("", n, value);
	}

}
