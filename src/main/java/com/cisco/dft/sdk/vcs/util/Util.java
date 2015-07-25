package com.cisco.dft.sdk.vcs.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Util {

	public static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

	private Util() {}

	public static void redirectLogError(String msg, Throwable t) {
		LOGGER.error(msg, t);
	}

	public static String executeCommand(String command, String...parameters) throws IOException {

		StringBuilder output = new StringBuilder();

		Process p = Runtime.getRuntime().exec(getCommand(command, parameters));

		try {
			p.waitFor();
		} catch (InterruptedException e) {
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

		String line = "";

		while ((line = reader.readLine()) != null) {
			output.append(line);
			output.append("\n");
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
		
		if (map.containsKey(key)) {
			return map.get(key);
		} else {
			return defaultV;
		}
		
	}

}
