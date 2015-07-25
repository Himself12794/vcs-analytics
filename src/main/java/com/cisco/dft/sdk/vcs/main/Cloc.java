package com.cisco.dft.sdk.vcs.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.dft.sdk.vcs.common.OSType;
import com.cisco.dft.sdk.vcs.common.Util;

public final class Cloc {

	private static final Logger LOGGER = LoggerFactory.getLogger("CLOC");

	private static boolean init = false;
	
	private static final String CLOC_DIR = "cloc/";

	public static final File SRC_DIR = new File(Util.class.getClassLoader()
			.getResource(CLOC_DIR).getPath());

	public static final File BIN_DIR = new File(CLOC_DIR);

	public static final String CLOC_EXE = "cloc-1.60.exe";

	public static final String CLOC_TAR = "cloc-1.60.tar";

	public static final String CLOC_PL = "cloc-1.60.pl";

	/**
	 * Initializes CLOC use. This must be called first, or CLOC will not be able
	 * to be used, regardless of access permissions.
	 */
	public static void init() {

		try {
			
			File file = getFileForOS(false);
			if (!file.exists()) {

				LOGGER.info("Making directory " + BIN_DIR.getAbsolutePath());
				FileUtils.forceMkdir(BIN_DIR);

				LOGGER.info("Extracting "
						+ getFileForOS(true).getAbsolutePath() + " to "
						+ getFileForOS(false).getAbsolutePath());
				
				InputStream link = (Util.class.getResourceAsStream("/" + CLOC_DIR
						+ getFileForOS(true).getName()));
				Files.copy(link, file.getAbsoluteFile().toPath());
			}
			
			init = true;
		} catch (IOException e) {
			
			LOGGER.error("CLOC initialization failed.", e);
			LOGGER.info("Defaulting to built-in analysis.");
			init = false;
		}

	}

	private static File getFileForOS(boolean src) {

		File file = src ? SRC_DIR : BIN_DIR;

		switch (OSType.getOSType()) {
			case MAC:
				return new File(file, CLOC_PL);
			case SOLARIS:
				return new File(file, CLOC_TAR);
			case UNIX:
				return new File(file, CLOC_TAR);
			default:
				return new File(file, CLOC_EXE);

		}

	}
	
	/**
	 * Checks if perl is installed
	 * 
	 * @return
	 */
	public static boolean isPerlInstalled() {

		if (!canGetCLOCStats()) { return false; }
		try {
			Util.executeCommand("perl", "-v");
			return true;
		} catch (IOException e) {
			LOGGER.error("Perl installation could not be detected", e);
			return false;
		}
	}
	
	/**
	 * Whether or not cloc use is possible
	 * 
	 * @return
	 */
	public static boolean canGetCLOCStats() {
		return init;
	}

	public static String getCLOCDataAsYaml(File file) throws IOException {

		if (!canGetCLOCStats()) { throw new IOException("Cannot run CLOC"); }

		return Util.executeCommand(getFileForOS(false).getPath(), "--yaml",
				"--skip-win-hidden", file.getAbsolutePath());
	}
	
	private Cloc() {}

}
