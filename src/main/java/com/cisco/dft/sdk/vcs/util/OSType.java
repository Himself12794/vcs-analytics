package com.cisco.dft.sdk.vcs.util;

/**
 * Borrowed from Mekong.com. Detects OS type.
 * 
 */
public enum OSType {
	
	WIN, MAC, UNIX, SOLARIS, UNKNOWN;
	
	private static final String OS = System.getProperty("os.name").toLowerCase();
	
	public boolean isWindows() { return this == WIN; }
 
	public boolean isMac() { return this == MAC; }
 
	public boolean isUnix() { return this == UNIX; }
 
	public boolean isSolaris() { return this == UNIX; }
	
	public boolean isUnknown() { return this == UNKNOWN; }
	
	@Override
	public String toString() {
		return "OS Type: " + this.name();
	}
	
	public static OSType getOSType() {
		
		if (OS.indexOf("win") >= 0) { return WIN; }
		else if (OS.indexOf("mac") >= 0) { return MAC; }
		else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 ) { return UNIX; }
		else if (OS.indexOf("sunos") >= 0) { return SOLARIS; }
		else { return UNKNOWN; }
		
	}

}
