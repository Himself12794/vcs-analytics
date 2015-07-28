package com.cisco.dft.sdk.vcs.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommandLineUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineUtils.class);

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

	private CommandLineUtils() {}
	
}
