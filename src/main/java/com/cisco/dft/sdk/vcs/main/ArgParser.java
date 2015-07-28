package com.cisco.dft.sdk.vcs.main;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.dft.sdk.vcs.common.Util;
import com.google.common.collect.Maps;

/**
 * An argument parser. The first argument is interpreted as an action, unless it
 * begins with "-" or "--", then the command is actionless. Second argument is
 * parsed as the command parameter, which is required as well. It follows the
 * same parsing rules as the action.
 * <p>
 * Any other arguments are interpreted as optional parameters. If the option
 * begins with --, an equals sign can be used to set its value in the map. If
 * there is no equals sign, the value is set as true. Arguments beginning with
 * just "-" are seen as flags, and pass a value of true into the map if they are
 * present
 * </p>
 * <p>
 * Options are not location specific, but the command and command parameter are.
 * If the first or second argument are determined to not be a command and
 * parameter, it will be parsed as an option instead.
 * </p>
 * 
 * @author phwhitin
 *
 */
// TODO add multiple parameters, accessible by location
public final class ArgParser {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ArgParser.class);

	private static final String ACTION = "action";

	private static final String ACTION_PARAM = "actionParam";

	private Map<String, String> values = Maps.newHashMap();

	private ArgParser() {
	}

	public static ArgParser parse(String[] args) {

		LOGGER.debug("Parsing args " + Arrays.toString(args));

		ArgParser parser = new ArgParser();

		if (args.length > 0) {

			parser.paramParser(args[0], true);

			LOGGER.debug("Found command " + args[0]);

			for (int i = 1; i < args.length; i++) {

				if (isOption(args[i])) {

					String[] value = args[i].replaceFirst("--", "").split("=");

					LOGGER.debug("Found option "
							+ (value.length > 1 ? value[0] : "no value"));

					if (value.length > 1) {
						Util.putIfAbsent(parser.values, value[0].toLowerCase(),
								value[1]);
					} else if (value.length > 0) {
						Util.putIfAbsent(parser.values, value[0].toLowerCase(),
								Boolean.TRUE.toString());
					}

				} else if (isFlag(args[i])) {

					LOGGER.debug("Found flag " + args[i]);

					Util.putIfAbsent(parser.values,
							args[i].replaceFirst("-", ""),
							Boolean.TRUE.toString());

				} else {

					LOGGER.debug("Found command parameter " + args[i]);

					Util.putIfAbsent(parser.values, ACTION_PARAM, args[i]);
				}

			}

		}
		return parser;
	}

	private static boolean isOption(String value) {
		return value.startsWith("--");
	}

	private static boolean isFlag(String value) {
		return value.length() == 2 && value.lastIndexOf('-') == 0;
	}

	private void paramParser(String param, boolean isAction) {

		final String key = isAction ? ACTION : ACTION_PARAM;

		if (!param.startsWith("--") && !param.startsWith("-")) {
			values.put(key, param);
		}

	}

	/**
	 * Gets the action command. This is the first argument given.
	 * 
	 * if the argument is prefixed by "-" or "--", this is null.
	 * 
	 * @return the command string, or null if no command detected
	 */
	public String getAction() {
		return values.get(ACTION);
	}

	/**
	 * Gets the action parameter, the second value.
	 * 
	 * Follows the same rule as the {@link ArgParser#getAction()}, except for
	 * the second argument. This is not location sensitive.
	 * 
	 * @return the command parameter, or null if non-existent
	 */
	public String getActionParameter() {
		return values.get(ACTION_PARAM);
	}

	/**
	 * Gets the option as a boolean value. This can be a flag, (-f, -v, etc.) or
	 * an option with --value=true or --value=false, or just --value for true.
	 * 
	 * @param key
	 * @return true if the flag exists or option is set to true, false if
	 *         non-existant or option is set to false
	 */
	public boolean getBoolean(String key) {
		return Boolean.parseBoolean((String) Util.getOrDefault(values, key,
				Boolean.FALSE.toString()));
	}

	/**
	 * Trys to get the mapped value as an integer. If it does not exist or is
	 * not an integer, returns null
	 * 
	 * @param key
	 * @return
	 */
	public Integer getInt(String key) {
		Integer num = null;

		try {
			num = Integer.valueOf(values.get(key));
		} catch (NumberFormatException e) {
			// No action needed
		}

		return num;
	}

	/**
	 * Tries to get the key as a Long, then tries to instantiate a new class of
	 * type V. If fails, returns null.
	 * 
	 * @param key
	 * @param k
	 * @param v
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <V> V getLongAsType(String key, Class<V> v) {
		
		Long value = getLong(key);
		
		if (value != null) {
			for (Constructor<?> c : v.getConstructors()) {
				
				if (c.getParameterTypes().length == 1) {
					
					for (Class<?> clazz : c.getParameterTypes()) {
						
						if (clazz.equals(Long.class) || "long".equals(clazz.getName())) {
							try {
								return (V) c.newInstance(value);
							} catch (Exception e) {
								LOGGER.debug("Failure in type conversion", e);
							}
						}
						
					}
					
				}
				
			}
		}
		
		return null;
		
	}

	/**
	 * Trys to get the mapped value as a long. If it does not exist or is not a
	 * long, returns 0
	 * 
	 * @param key
	 * @return
	 */
	public Long getLong(String key) {
		Long num = null;

		try {
			num = Long.valueOf(values.get(key));
		} catch (NumberFormatException e) {
			// No action needed
		}

		return num;
	}

	/**
	 * Gets the property as a string. Returns default value if value is null.
	 * 
	 * @param key
	 * @return value of key or the default
	 */
	public String getString(String key, String defaultV) {
		return Util.ifNullDefault(getString(key), defaultV);
	}

	/**
	 * Gets the property as a string.
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return values.get(key);
	}

	/**
	 * Gets the action command as an enum constant. Because I like enums, I made
	 * this. Not case sensitive.
	 * 
	 * @param t
	 * @return the constant if it exists, or null if not
	 */
	public <T extends Enum<?>> T getActionAsEnum(Class<T> t) {

		for (T vals : t.getEnumConstants()) {
			if (vals.name().equalsIgnoreCase(getAction())) { return vals; }
		}

		return null;

	}

}
