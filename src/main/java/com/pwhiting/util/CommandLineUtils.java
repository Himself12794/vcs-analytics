package com.pwhiting.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities used to call commands.
 * 
 * @author phwhitin
 *
 */
public final class CommandLineUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineUtils.class);

	private CommandLineUtils() {
	}

	public static String executeCommand(final String command, final String... parameters) throws IOException {

		final StringBuilder output = new StringBuilder();

		final Process p = Runtime.getRuntime().exec(getCommand(command, parameters));

		try {
			p.waitFor();
		} catch (final InterruptedException e) {
			// No action needed
		}

		final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

		String line = "";

		try {

			while ((line = reader.readLine()) != null) {
				output.append(line);
				output.append("\n");
			}

		} catch (final Exception e) {
			LOGGER.error("An error occured in command execution", e);
		} finally {
			reader.close();
		}

		return output.toString();

	}

	private static String getCommand(final String command, final String[] parameters) {

		final StringBuilder output = new StringBuilder(command);
		output.append(" ");

		for (final String parameter : parameters) {

			output.append(parameter);
			output.append(" ");
		}

		return output.toString();

	}

}
