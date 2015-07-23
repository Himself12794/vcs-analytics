package com.cisco.dft.sdk.vcs.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Util {
	
	public static final File CLOC_EXE = new File("src/main/resources/cloc-1.60.exe");

	public static final File CLOC_TAR = new File("src/main/resources/cloc-1.60.tar");
	
	public static final File CLOC_PL = new File("src/main/resources/cloc-1.60.pl");
	
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
		 
		StringBuffer output = new StringBuffer();
 
		Process p;
		try {
			p = Runtime.getRuntime().exec(getCommand(command, parameters));
			p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));
 
                        String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
 
		} catch (Exception e) {
			e.printStackTrace();
		}
 
		return output.toString();
 
	}
	
	private static String getCommand(String command, String[] parameters) {
		
		StringBuffer output = new StringBuffer(command);
		output.append(" ");
		
		for (String parameter : parameters) {
			
			output.append(parameter);
			output.append(" ");
			
		}
		
		return output.toString();
		
	}

}
