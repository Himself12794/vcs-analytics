package com.cisco.dft.sdk.vcs.main;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.dft.sdk.vcs.common.util.Util;
import com.google.common.collect.Lists;
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
public final class ArgParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArgParser.class.getSimpleName());

	private final Map<String, String> values = Maps.newHashMap();

	private String action;

	private final List<String> actionParams = Lists.newArrayList();

	private ArgParser() {
	}

	/**
	 * Checks if a specific parameter exists.
	 * <p>
	 * Don't confuse this with {@link ArgParser#getBoolean(String)}, it is not
	 * the same thing. This returns a action paramter, i.e. any value following
	 * the action command that is not a flag or option.
	 *
	 * @param param
	 * @return
	 */
	public boolean actionParameterExists(final String param) {
		return actionParams.contains(param);
	}

	/**
	 * Checks the value, and if it is a valid command, stores it as the action
	 * 
	 * @param param
	 */
	private void commandParser(final String param) {

		if (!param.startsWith("--") && !param.startsWith("-")) {
			action = param;
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
		return action;
	}

	/**
	 * Gets the action command as an enum constant. Because I like enums, I made
	 * this. Not case sensitive.
	 *
	 * @param t
	 * @return the constant if it exists, or null if not
	 */
	public <T extends Enum<?>> T getActionAsEnum(final Class<T> t) {

		for (final T vals : t.getEnumConstants()) {
			if (vals.name().equalsIgnoreCase(getAction())) { return vals; }
		}

		return null;

	}

	/**
	 * If parameter exists, return it, else null. Works similar to
	 * {@link ArgParser#actionParameterExists(String)} except instead of
	 * verification, it returns the value.
	 *
	 * @param param
	 * @return param if it exists, else null
	 */
	public String getActionParameter(final String param) {
		return actionParams.contains(param) ? param : null;
	}

	/**
	 * Gets action parameter by location. Index starts at 0.
	 *
	 * @param i
	 * @return
	 */
	public String getActionParameterByIndex(final int i) {
		return actionParams.size() > i ? actionParams.get(i) : null;
	}

	/**
	 * Gets the option as a boolean value. This can be a flag, (-f, -v, etc.) or
	 * an option with --value=true or --value=false, or just --value for true.
	 *
	 * @param key
	 * @return true if the flag exists or option is set to true, false if
	 *         non-existant or option is set to false
	 */
	public boolean getBoolean(final String key) {
		return Boolean.parseBoolean(Util.getOrDefault(values, key, Boolean.FALSE.toString()));
	}

	/**
	 * Trys to get the mapped value as an integer. If it does not exist or is
	 * not an integer, returns null
	 *
	 * @param key
	 * @return
	 */
	public Integer getInt(final String key) {
		Integer num = null;

		try {
			num = Integer.valueOf(values.get(key));
		} catch (final NumberFormatException e) {
			// No action needed
		}

		return num;
	}

	/**
	 * Trys to get the mapped value as a long. If it does not exist or is not a
	 * long, returns 0
	 *
	 * @param key
	 * @return
	 */
	public Long getLong(final String key) {
		Long num = null;

		try {
			num = Long.valueOf(values.get(key));
		} catch (final NumberFormatException e) {
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
	public <V> V getLongAsType(final String key, final Class<V> v) {

		final Long value = getLong(key);

		if (value != null) {
			for (final Constructor<?> c : v.getConstructors()) {

				if (c.getParameterTypes().length == 1) {

					for (final Class<?> clazz : c.getParameterTypes()) {

						if (clazz.equals(Long.class) || "long".equals(clazz.getName())) {
							try {
								return (V) c.newInstance(value);
							} catch (final Exception e) {
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
	 * Gets the property as a string.
	 *
	 * @param key
	 * @return
	 */
	public String getString(final String key) {
		return values.get(key);
	}

	/**
	 * Gets the property as a string. Returns default value if value is null.
	 *
	 * @param key
	 * @return value of key or the default
	 */
	public String getString(final String key, final String defaultV) {
		return Util.ifNullDefault(getString(key), defaultV);
	}

	private static boolean isFlag(final String value) {
		return value.length() == 2 && value.lastIndexOf('-') == 0;
	}

	private static boolean isOption(final String value) {
		return value.startsWith("--");
	}

	public static ArgParser parse(final String[] args) {

		LOGGER.debug("Parsing args " + Arrays.toString(args));

		final ArgParser parser = new ArgParser();

		if (args.length > 0) {

			parser.commandParser(args[0]);

			LOGGER.debug("Found command " + args[0]);

			for (int i = 1; i < args.length; i++) {

				if (isOption(args[i])) {

					final String[] value = args[i].replaceFirst("--", "").split("=");

					LOGGER.debug("Found option " + (value.length > 0 ? value[0] : "no value"));

					if (value.length > 1) {
						Util.putIfAbsent(parser.values, value[0].toLowerCase(), value[1]);
					} else if (value.length > 0) {
						Util.putIfAbsent(parser.values, value[0].toLowerCase(),
								Boolean.TRUE.toString());
					}

				} else if (isFlag(args[i])) {

					LOGGER.debug("Found flag " + args[i]);

					Util.putIfAbsent(parser.values, args[i].replaceFirst("-", ""),
							Boolean.TRUE.toString());

				} else {
					LOGGER.debug("Found command parameter " + args[i]);
					parser.actionParams.add(args[i]);
				}

			}

		}
		return parser;
	}

}
