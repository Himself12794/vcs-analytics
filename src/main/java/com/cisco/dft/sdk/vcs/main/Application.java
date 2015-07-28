package com.cisco.dft.sdk.vcs.main;

import java.io.PrintStream;
import java.util.Date;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.dft.sdk.vcs.common.Util;
import com.cisco.dft.sdk.vcs.core.AuthorInfoBuilder;
import com.cisco.dft.sdk.vcs.core.BranchInfo;
import com.cisco.dft.sdk.vcs.core.GitRepo;
import com.google.common.collect.Range;

/**
 * The application class.
 * 
 * @author phwhitin
 *
 */
public final class Application {

	public static final String VERISION = "v1.0";

	private static final Logger LOGGER = LoggerFactory.getLogger("Application");

	private static final PrintStream out = System.out;

	private static final PrintStream err = System.err;

	/** The singleton app instance */
	private static final Application APPLICATION = new Application();

	private ProgramConfig config;

	private Application() {
		setConfig(ProgramConfig.HELP);
	}

	/**
	 * Sets the config parameters. Cannot be null.
	 * 
	 * @param config
	 * @return
	 */
	private Application setConfig(ProgramConfig config) {
		LOGGER.trace("Setting configuration as " + config);
		if (config != null) {
			this.config = config;
		}
		return this;
	}

	private ProgramConfig getConfig() {
		return config;
	}

	/**
	 * Runs the program with the given config parameters.
	 * 
	 * @throws GitAPIException
	 */
	void execute() throws GitAPIException {
		
		if (config.isDebugEnabled()) { Util.enableDebugLogging(); }

		LOGGER.debug("Executing with params: " + config.toString());

		if (config.shouldShowVersion()) {
			out.println(VERISION);
		}

		switch (config.getAction()) {
			case ANALYZE:
				analyze();
				break;
			case INIT:
				init();
				break;
			case DEBUG:
				debug();
				break;
			case HELP:
			default:
				help();
				break;

		}

	}

	/**
	 * Initializes Cloc.
	 */
	private void init() {
		Cloc.init();
	}

	/**
	 * Prints the help message to stdout.
	 * 
	 */
	public void help() {
		out.println(ProgramConfig.getUsage());
	}

	/**
	 * Runs with some debug data and preset options and parameters.
	 * 
	 * @throws GitAPIException
	 */
	private void debug() throws GitAPIException {
		setConfig(ProgramConfig.DEBUG).analyze();
	}

	private void analyze() throws GitAPIException {

		if (config.getUrl() == null) {

			out.println("Usage: analyze <url>");

		} else {

			init();

			UsernamePasswordCredentialsProvider cp = new UsernamePasswordCredentialsProvider(config
					.getUsername(), config.getPassword());

			GitRepo repo = new GitRepo(config.getUrl(), config.getBranch(), false, cp);
			repo.sync(config.getBranch(), config.shouldGenerateStats(),
					config.shouldUseCloc());

			if (!(config.getStart() == null && config.getEnd() == null)) {

				if (config.getBranch() != null) {
					printLimitedRange(repo.getRepoStatistics()
							.getBranchInfoFor(config.getBranch()));
				} else {
					printLimitedRange(repo.getRepoStatistics().getBranchInfos());
				}

			} else {
				out.println(repo.getRepoStatistics().toString(config.shouldShowCommits()));
			}

			repo.close();

		}

	}

	private void printLimitedRange(BranchInfo... bis) {

		debugRange();

		for (BranchInfo bi : bis) {

			AuthorInfoBuilder aib = bi.getAuthorStatistics().limitToDateRange(
					getAppropriateRange(config.getStart(), config.getEnd()));
			out.println(aib);

		}
	}

	private void debugRange() {
		LOGGER.debug("Limiting data to range: "
				+ (config.getStart() == null ? "first-commit" : config
						.getStart())
				+ " - "
				+ (config.getEnd() == null ? "most-recent-commit" : config
						.getEnd()));
	}

	private Range<Date> getAppropriateRange(Date start, Date end) {

		if (start != null && end != null) {

			final int c = start.compareTo(end);

			if (c < 0) {
				return Range.closed(start, end);
			} else if (c > 0) {
				return Range.closed(end, start);
			} else {
				return Range.singleton(start);
			}

		} else if (start == null && end != null) {
			return Range.atMost(end);
		} else if (start != null && end == null) {
			return Range.atLeast(start);
		} else {
			return Range.all();
		}

	}

	public static void main(String[] args) throws GitAPIException {

		try {
			APPLICATION.setConfig(ProgramConfig.parseArgs(args)).execute();
		} catch (Exception e) {
			err.println("An error occurred during application execution: "
					+ e.getMessage());
			LOGGER.debug("Error: ", e);
		}

		// TODO cloc analysis - full testing
		// TODO clean output

	}

	static Application getInstance() {
		return APPLICATION;
	}

	public static ProgramConfig getConfiguration() {
		return APPLICATION.getConfig();
	}

	static Application setConfiguration(ProgramConfig config) {
		getInstance().setConfig(config);
		return getInstance();
	}

}
