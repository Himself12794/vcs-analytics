package com.cisco.dft.sdk.vcs.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

import com.google.common.collect.Range;

/**
 * Utilities for use throughout the rest of the Application
 *
 * @author phwhitin
 *
 */
public final class Util {

	private static final Logger LOGGER = LoggerFactory.getLogger("Utilities");

	private static final int DEFAULT_ALLOWED_NAME_SIZE = 20;

	private Util() {
	}

	/**
	 * Determines the type of range to return.
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	public static Range<Date> getAppropriateRange(final Date start, final Date end) {

		if (nonNullBoth(start, end)) {
			return nonNullComparison(start, end);
		} else if (nullFirstOnly(start, end)) {
			return Range.atMost(end);
		} else if (nullLastOnly(start, end)) {
			return Range.atLeast(start);
		} else {
			return Range.all();
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
	public static <K, V> V getOrDefault(final Map<K, V> map, final K key, final V defaultV) {

		if (map.containsKey(key)) {
			return map.get(key);
		} else {
			return defaultV;
		}

	}

	/**
	 * If {@code arg0} is null, returns {@code arg1}, otherwise returns
	 * {@code arg0}.
	 *
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public static <T> T ifNullDefault(final T arg0, final T arg1) {
		if (arg0 == null) {
			return arg1;
		} else {
			return arg0;
		}
	}

	/**
	 * This checks the range and swaps them if they were given in the wrong
	 * order.
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	public static Range<Date> nonNullComparison(final Date start, final Date end) {

		final int c = start.compareTo(end);

		if (c < 0) {
			return Range.closed(start, end);
		} else if (c > 0) {
			return Range.closed(end, start);
		} else {
			return Range.singleton(start);
		}

	}

	public static boolean nonNullBoth(final Object arg0, final Object arg1) {
		return arg0 != null && arg1 != null;
	}

	public static boolean nullFirstOnly(final Object arg0, final Object arg1) {
		return arg0 == null && arg1 != null;
	}

	public static boolean nullLastOnly(final Object arg0, final Object arg1) {
		return arg0 != null && arg1 == null;
	}
	
	public static void printNTimes(final char value, final int n, final boolean lineBreak) {
		System.out.print(getNTimes(value, n, lineBreak));
	}

	public static String getNTimes(final char value, final int n, final boolean lineBreak) {
		return valueWithFiller("", n, value) + (lineBreak ? "\n" : "");
	}

	/**
	 * If the value does not exist for the key, it places it in the map.
	 *
	 * @param map
	 * @param key
	 * @param value
	 * @return the value "value", mutates parameter "map"
	 */
	public static <K, V> V putIfAbsent(final Map<K, V> map, final K key, final V value) {

		if (map.containsKey(key)) {
			return map.get(key);
		} else {
			map.put(key, value);
			return value;
		}

	}

	/**
	 * When you're too lazy to create a logger for each class.
	 *
	 * @param msg
	 * @param t
	 */
	public static void redirectLogError(final String msg, final Throwable t) {
		LOGGER.error(msg, t);
	}

	/**
	 * Enables debug logging
	 */
	public static void setLoggingLevel(final Level level) {
		final ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(Logger.ROOT_LOGGER_NAME);
		root.setLevel(level);
		LOGGER.debug("Enabled " + level.toString() + " logging");
	}

	/**
	 * Gets the string form of the obj, ensuring it is no longer than the given
	 * size, and if it is less, fills the rest of with spaces.
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

	/**
	 * {@link Util#valueWithSpaces(Object, int)} with size set to 20.
	 *
	 * @param x
	 * @return
	 */
	public static String valueWithSpaces(final Object x) {
		return valueWithFiller(x, DEFAULT_ALLOWED_NAME_SIZE, ' ');
	}
	
	public static <K> void incrementInMap(Map<K, Integer> map, K k, int v) {
		
		if (map.containsKey(k)) {
			map.put(k, map.get(k) + v);
		} else {
			map.put(k, v);
		}
		
	}

	public static <T> String toString(final T t) {

		final Class<?> clazz = t.getClass();

		final StringBuilder value = new StringBuilder(clazz.getSimpleName());
		value.append("[");

		for (final Field field : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			try {
				field.setAccessible(true);
				value.append(field.getName() + "=" + field.get(t) + ", ");
				field.setAccessible(false);
			} catch (final Exception e) {
				redirectLogError("An unexpected error occured in value mapping", e);
			}
		}

		value.append("]");

		return value.toString();

	}

}
