package com.cisco.dft.sdk.vcs.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

public final class Util {

	private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

	private Util() {}
	
	public static void enableDebugLogging() {
		LOGGER.info("Enabling debug logging");
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	    root.setLevel(Level.DEBUG);
	}

	public static void redirectLogError(String msg, Throwable t) {
		LOGGER.error(msg, t);
	}

	public static String executeCommand(String command, String...parameters) throws IOException {

		StringBuilder output = new StringBuilder();

		Process p = Runtime.getRuntime().exec(getCommand(command, parameters));

		try {
			p.waitFor();
		} catch (InterruptedException e) {
			// No action needed
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

		String line = "";
		
		try {
			
			while ((line = reader.readLine()) != null) {
				output.append(line);
				output.append("\n");
			}
			
		} catch (Exception e) {
			LOGGER.error("An error occured in command execution", e);
		} finally {
			reader.close();
		}

		return output.toString();

	}

	private static String getCommand(String command, String[] parameters) {

		StringBuilder output = new StringBuilder(command);
		output.append(" ");

		for (String parameter : parameters) {

			output.append(parameter);
			output.append(" ");
		}

		return output.toString();

	}

	/**
	 * If the value exists for the key, it places it in the map, otherwise it
	 * places the given value.
	 * 
	 * @param map
	 * @param key
	 * @param value
	 * @return the current value, or the given value if non-existent
	 */
	public static <V, K> V putIfAbsent(Map<K, V> map, K key, V value) {

		if (map.containsKey(key)) {
			return map.get(key);
		} else {
			map.put(key, value);
			return value;
		}

	}
	
	public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultV) {
		return getOrDefault(map, key, defaultV, false);
	}
	
	public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultV, boolean nullCheck) {
		
		if (map.containsKey(key)) {
			return nullCheck && map.get(key) == null ? defaultV : map.get(key);
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
