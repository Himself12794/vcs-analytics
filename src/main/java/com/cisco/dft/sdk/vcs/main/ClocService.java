package com.cisco.dft.sdk.vcs.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.cisco.dft.sdk.vcs.common.CodeSniffer.Language;
import com.cisco.dft.sdk.vcs.common.CommandLineUtils;
import com.cisco.dft.sdk.vcs.common.OSType;
import com.cisco.dft.sdk.vcs.common.Util;
import com.cisco.dft.sdk.vcs.core.ClocData;
import com.cisco.dft.sdk.vcs.core.ClocData.Header;
import com.cisco.dft.sdk.vcs.core.ClocData.LangStats;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.common.collect.Maps;

/**
 * Utility class for CLOC use.
 * 
 * @author phwhitin
 *
 */
public final class ClocService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClocService.class.getSimpleName());

	private static boolean init = false;
	
	private static boolean clocInstalled = false;
	
	private static boolean perlInstalled = false;

	private static final String CLOC_DIR = "cloc/";

	public static final File SRC_DIR = new File(Util.class.getClassLoader()
			.getResource(CLOC_DIR).getPath());

	public static final File BIN_DIR = new File(FileUtils.getTempDirectory(), "vcs-analytics/" + CLOC_DIR);

	public static final String CLOC_EXE = "cloc-1.60.exe";

	public static final String CLOC_PL = "cloc-1.60.pl";

	/**
	 * Initializes CLOC use. This must be called first, or CLOC will not be able
	 * to be used, regardless of installations or access permissions.
	 */
	public static void init() {
		
		if (init) { return; }
		
		if (isClocInstalled()) {
			
			clocInstalled = true;
			init = true;
			
		} else if (isPerlInstalled()) {
			
			perlInstalled = true;
			init = true;
			
		} else {
			
			if (!OSType.getOSType().isWindows()) {
				init = false;
			} else {
		
				try {
		
					File file = getFileForOS(false);
					if (!file.exists()) {
		
						LOGGER.debug("Making directory " + BIN_DIR.getAbsolutePath());
						FileUtils.forceMkdir(BIN_DIR);
		
						LOGGER.debug("Extracting "
								+ getFileForOS(true).getName() + " to "
								+ getFileForOS(false).getAbsolutePath());
		
						InputStream link = (Util.class.getResourceAsStream("/"
								+ CLOC_DIR + getFileForOS(true).getName()));
						Files.copy(link, file.getAbsoluteFile().toPath());
					}
		
					init = true;
					
				} catch (IOException e) {
					LOGGER.trace("cloc initialization failed", e);
					LOGGER.warn("cloc initialization failed");
					LOGGER.info("Install perl and/or cloc onto your system to use cloc statistics");
					init = false;
				}
			}
		}

	}
	
	/**
	 * Run with true to get classpath location, false for tmp directory
	 * @param src
	 * @return
	 */
	private static File getFileForOS(boolean src) {

		File file = src ? SRC_DIR : BIN_DIR;

		switch (OSType.getOSType()) {
			case WIN:
				return new File(file, CLOC_EXE);
			case MAC:
			case SOLARIS:
			case UNIX:
			default:
				return new File(file, CLOC_PL);

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
			CommandLineUtils.executeCommand("perl", "-v");
			return true;
		} catch (IOException e) {
			LOGGER.trace("An error occured in perl detection", e);
			LOGGER.info("Perl not detected");
			return false;
		}
	}
	
	public static boolean isClocInstalled() {
		
		try {
			LOGGER.info( "Cloc detected, version is: " + CommandLineUtils.executeCommand("cloc", "-version") );
			return true;
		} catch (IOException e) {
			LOGGER.trace("Cloc not detected", e);
			LOGGER.info( "Cloc not detected" );
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

		if (!canGetCLOCStats()) { throw new IOException("Cannot run cloc"); }

		if (clocInstalled) {
			LOGGER.debug("Cloc already exists, using that instead. Running on directory " + file.getAbsolutePath());
			return CommandLineUtils.executeCommand("cloc", "--yaml", file.getAbsolutePath());
		} else if (perlInstalled) {
			return CommandLineUtils.executeCommand("perl", CLOC_PL, "--yaml", "--skip-win-hidden");
		} else {
			return CommandLineUtils.executeCommand(getFileForOS(false).getPath(), "--yaml","--skip-win-hidden", file.getAbsolutePath());
		}
				
	}

	/**
	 * Gets CLOC statistics from a directory and parses to a CLOCData instance.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static ClocData getClocStatistics(File file) throws IOException {
		
		if (!canGetCLOCStats()) { throw new IOException("Cannot run CLOC"); }

		LOGGER.debug("Getting cloc statistics for directory " + file.getAbsolutePath());
		
		String yamlStr = getCLOCDataAsYaml(file);
		
		Map<Language, LangStats> langStats = Maps.newHashMap();

		Header header = new Header();

		Iterable<Object> yaml = new Yaml().loadAll(yamlStr);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

		for (Object obj : yaml) {

			if (obj instanceof Map) {

				Map<String, Object> map = ((Map<String, Object>) obj);

				for (Entry<String, Object> entry : map.entrySet()) {

					String key = entry.getKey();

					if ("header".equals(key)) {
						header = mapper.convertValue(entry.getValue(),
								Header.class);
					} else if (!"SUM".equals(key)) {
						LangStats langStat = mapper.convertValue(
								entry.getValue(), LangStats.class);
						Language lang = Language.getType(key);
						langStat.setLanguage(lang);
						langStats.put(lang, langStat);
					}

				}
			}

		}

		return new ClocData(header, langStats);
	}

	private ClocService() {
	}

}
