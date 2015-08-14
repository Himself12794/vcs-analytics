package com.cisco.dft.sdk.vcs.core;

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

import com.cisco.dft.sdk.vcs.core.ClocData.Header;
import com.cisco.dft.sdk.vcs.core.ClocData.LangStats;
import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;
import com.cisco.dft.sdk.vcs.util.CommandLineUtils;
import com.cisco.dft.sdk.vcs.util.OSType;
import com.cisco.dft.sdk.vcs.util.Util;
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

	private static final String[] ARGS = { "", "--quiet", "--progress-rate=0", "--yaml",
			"--skip-win-hidden", "" };

	private static boolean init = false;

	private static boolean clocInstalled = false;

	private static boolean perlInstalled = false;

	private static final String CLOC_DIR = "cloc/";

	public static final File BIN_DIR = new File(FileUtils.getTempDirectory(), "vcs-analytics/"
			+ CLOC_DIR);

	public static final String CLOC_EXE = "cloc-1.60.exe";

	public static final String CLOC_PL = "cloc-1.60.pl";

	private ClocService() {
	}

	/**
	 * Whether or not cloc use is possible
	 *
	 * @return
	 */
	public static boolean canGetCLOCStats() {
		return init;
	}

	public static String getCLOCDataAsYaml(final File file) throws IOException {

		if (!canGetCLOCStats()) { throw new IOException("Cannot run cloc"); }

		ARGS[ARGS.length - 1] = file.getAbsolutePath();

		if (clocInstalled) {
			LOGGER.debug("Running CLOC on directory {}", file);
			return CommandLineUtils.executeCommand("cloc", ARGS);
		} else if (perlInstalled) {
			ARGS[0] = CLOC_PL;
			return CommandLineUtils.executeCommand("perl", ARGS);
		} else {
			return CommandLineUtils.executeCommand(getFileForOS(false).getPath(), ARGS);
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
	public static ClocData getClocStatistics(final File file) throws IOException {

		if (!canGetCLOCStats()) { throw new IOException("Cannot run CLOC"); }

		final String yamlStr = getCLOCDataAsYaml(file);

		final Map<Language, LangStats> langStats = Maps.newHashMap();

		Header header = new Header();

		final Iterable<Object> yaml = new Yaml().loadAll(yamlStr);

		final ObjectMapper mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

		for (final Object obj : yaml) {

			if (obj instanceof Map) {

				final Map<String, Object> map = (Map<String, Object>) obj;

				for (final Entry<String, Object> entry : map.entrySet()) {

					final String key = entry.getKey();

					if ("header".equals(key)) {
						header = mapper.convertValue(entry.getValue(), Header.class);
					} else if (!"SUM".equals(key)) {
						final LangStats langStat = mapper.convertValue(entry.getValue(),
								LangStats.class);
						final Language lang = Language.getType(key);
						langStat.setLanguage(lang);
						langStats.put(lang, langStat);
					}

				}
			}

		}

		return new ClocData(header, langStats);
	}

	/**
	 * Run with true to get classpath location, false for tmp directory
	 *
	 * @param src
	 * @return
	 */
	private static File getFileForOS(final boolean src) {

		final File file = src ? getClassPathFile(CLOC_DIR) : BIN_DIR;

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

					final File file = getFileForOS(false);
					if (!file.exists()) {

						LOGGER.debug("Making directory " + BIN_DIR.getAbsolutePath());
						FileUtils.forceMkdir(BIN_DIR);

						LOGGER.debug("Extracting " + getFileForOS(true).getName() + " to "
								+ getFileForOS(false).getAbsolutePath());

						final InputStream link = Util.class.getResourceAsStream("/" + CLOC_DIR
								+ getFileForOS(true).getName());
						Files.copy(link, file.getAbsoluteFile().toPath());
					}

					init = true;

				} catch (final IOException e) {
					LOGGER.warn("CLOC initialization failed");
					LOGGER.trace("", e);
					LOGGER.info("You must enable execute permissions for this program, or manually install CLOC for its integration to work.");
					init = false;
				}
			}
		}

	}

	public static boolean isClocInstalled() {

		try {
			LOGGER.debug("CLOC version "
					+ CommandLineUtils.executeCommand("cloc", "-version").replace("\n", " ")
					+ "detected.");
			return true;
		} catch (final IOException e) {
			LOGGER.debug("CLOC not detected");
			LOGGER.trace("", e);
			return false;
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
		} catch (final IOException e) {
			LOGGER.debug("Perl not detected");
			LOGGER.trace("", e);
			return false;
		}
	}

	private static File getClassPathFile(String path) {
		return new File(ClocService.class.getClassLoader().getResource(path).getPath());
	}

}
