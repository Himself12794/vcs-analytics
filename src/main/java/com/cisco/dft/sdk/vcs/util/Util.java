package com.cisco.dft.sdk.vcs.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Util {
	
	public static final File CLOC_EXE = new File("src/main/resources/bin/cloc-1.60.exe");

	public static final File CLOC_TAR = new File("src/main/resources/bin/cloc-1.60.tar");
	
	public static final File CLOC_PL = new File("src/main/resources/bin/cloc-1.60.pl");
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);
	
	private Util(){}
	
	public static void redirectLogError(String msg, Throwable t) {
		LOGGER.error(msg, t);
	}
	
	private static String getCommandForOS() {
		
		switch (OSType.getOSType()) {
			case MAC:
				return CLOC_PL.getPath();
			case SOLARIS:
				return CLOC_TAR.getPath();
			case UNIX:
				return CLOC_TAR.getPath();
			case WIN:
				return CLOC_EXE.getPath();
			default:
				return CLOC_EXE.getPath();
			
		}
		
	}
	
	public static String getCLOCDataAsYaml(File file) {

		return executeCommand(getCommandForOS(), new String[]{"--yaml", file.getAbsolutePath()});
	}
	
	public static String executeCommand(String command, String[] parameters) {
		 
		StringBuilder output = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			
			Process p = Runtime.getRuntime().exec(getCommand(command, parameters));
			p.waitFor();
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
 
			String line = "";
			
			while ((line = reader.readLine()) != null) {
				output.append(line);
				output.append("\n");
			}
 
		} catch (Exception e) {
			LOGGER.error("An error occured in running CLOC.", e);
		} finally {
			
			try {
				reader.close();
			} catch (IOException e) {
				LOGGER.error("An error occured in running CLOC.", e);
			}
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

}
