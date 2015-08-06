package com.cisco.dft.sdk.vcs.util;

/**
 * Borrowed from Mekong.com. Detects OS type.
 *
 */
public enum OSType {

	WIN, MAC, UNIX, SOLARIS, UNKNOWN;

	private static final String OS = System.getProperty("os.name").toLowerCase();

	public boolean isMac() {
		return this == MAC;
	}

	public boolean isSolaris() {
		return this == UNIX;
	}

	public boolean isUnix() {
		return this == UNIX;
	}

	public boolean isUnknown() {
		return this == UNKNOWN;
	}

	public boolean isWindows() {
		return this == WIN;
	}

	@Override
	public String toString() {
		return "OS Type: " + name();
	}

	public static OSType getOSType() {

		if (win()) {
			return WIN;
		} else if (mac()) {
			return MAC;
		} else if (unix()) {
			return UNIX;
		} else if (solaris()) {
			return SOLARIS;
		} else {
			return UNKNOWN;
		}

	}

	private static boolean mac() {
		return OS.indexOf("mac") >= 0;
	}

	private static boolean solaris() {
		return OS.indexOf("sunos") >= 0;
	}

	private static boolean unix() {
		return OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0;
	}

	private static boolean win() {
		return OS.indexOf("win") >= 0;
	}

}
