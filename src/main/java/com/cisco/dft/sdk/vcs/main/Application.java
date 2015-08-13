package com.cisco.dft.sdk.vcs.main;

import java.io.PrintStream;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import ch.qos.logback.classic.Level;

import com.cisco.dft.sdk.vcs.core.AuthorInfoBuilder;
import com.cisco.dft.sdk.vcs.core.BranchInfo;
import com.cisco.dft.sdk.vcs.core.ClocService;
import com.cisco.dft.sdk.vcs.core.GitRepo;
import com.cisco.dft.sdk.vcs.core.SVNRepo;
import com.cisco.dft.sdk.vcs.core.error.BranchNotFoundException;
import com.cisco.dft.sdk.vcs.util.Util;

/**
 * The application class. For total SDK abstraction, the library is still valid
 * with this package totally removed.
 *
 * @author phwhitin
 */
// TODO scrub unnecessary files from SVN information
public final class Application {

	public static final String VERISION = "v1.2.0";

	private static final Logger LOGGER = LoggerFactory.getLogger("Application");

	private static final PrintStream out = System.out;

	private static final PrintStream err = System.err;

	/** The singleton app instance */
	private static final Application APPLICATION = new Application();

	private ProgramConfig config;

	private Application() {
		setConfig(ProgramConfig.HELP);
	}

	private void analyze() throws GitAPIException, SVNException {

		if (config.getUrl() == null) {

			out.println();

		} else {

			init();

			if (config.shouldForceGit()) {
				analyzeAsGit();
			} else if (config.shouldForceSvn()) {
				analyzeAsSVN();
			} else {

				if (config.getUrl().endsWith(".git")) {
					analyzeAsGit();
				} else {
					analyzeAsSVN();
				}

			}

		}

	}

	private void analyzeAsGit() throws GitAPIException {

		final UsernamePasswordCredentialsProvider cp = new UsernamePasswordCredentialsProvider(config
				.getUsername(), config.getPassword());

		final GitRepo repo = new GitRepo(config.getUrl(), config.getBranch(), false, cp);
		repo.sync(config.getBranch(), config.shouldGenerateStats(), config.shouldUseCloc());

		if (!(config.getStart() == null && config.getEnd() == null)) {

			if (config.getBranch() != null) {
				printLimitedRange(repo.getRepoStatistics().getBranchInfoFor(config.getBranch()));
			} else {
				printLimitedRange(repo.getRepoStatistics().getBranchInfos());
			}

		} else {
			out.println(repo.getRepoStatistics().toString(config.shouldShowCommits()));
		}

		repo.close();

	}

	/**
	 * Treats the url as a svn repo
	 *
	 * @throws SVNException
	 * @throws BranchNotFoundException 
	 */
	private void analyzeAsSVN() throws SVNException, BranchNotFoundException {

		LOGGER.debug("running as svn");

		final SVNRepo repo = new SVNRepo(config.getUrl(), config.getBranch(), config.getUsername(), config
				.getPassword(), false, false);
		
		repo.sync(config.getBranch(), config.shouldGetLangStats(), config.shouldGenerateStats(), config.getRevA(), config.getRevB());

		if (!(config.getStart() == null && config.getEnd() == null)) {

			if (config.getBranch() != null) {
				printLimitedRange(repo.getRepoStatistics().getBranchInfoFor(config.getBranch()));
			} else {
				printLimitedRange(repo.getRepoStatistics().getBranchInfos());
			}

		} else {
			out.println(repo.getRepoStatistics().toString(config.shouldShowCommits()));
		}

	}

	/**
	 * Runs with some debug data and preset options and parameters.
	 *
	 * @throws GitAPIException
	 * @throws SVNException
	 */
	private void debug() throws GitAPIException, SVNException {
		setConfig(ProgramConfig.DEBUG).execute();
	}

	private void debugRange() {
		LOGGER.debug("Limiting data to range: "
				+ (config.getStart() == null ? "first-commit" : config.getStart()) + " - "
				+ (config.getEnd() == null ? "most-recent-commit" : config.getEnd()));
	}

	/**
	 * Runs the program with the given config parameters.
	 *
	 * @throws GitAPIException
	 * @throws SVNException
	 */
	void execute() throws GitAPIException, SVNException {

		if (config.shouldShowVersion()) {
			out.println(VERISION);
		}

		if (config.isDebugEnabled()) {
			Util.setLoggingLevel(Level.DEBUG);
		}

		LOGGER.debug("Executing with params: " + config.toString());

		if (!config.getAction().isValid(config)) {
			out.println(config.getAction().getUsage());
			return;
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

	private ProgramConfig getConfig() {
		return config;
	}

	/**
	 * Prints the help message to stdout.
	 */
	void help() {
		if (config.getUrl() == null) {
			out.println(ProgramConfig.getUsage());
		} else {

			try {
				final Action action = Action.valueOf(config.getUrl().toUpperCase());
				out.print(action.getUsage());
			} catch (final Exception e) {
				LOGGER.trace("Unrecognized help parameter", e);
				out.println(ProgramConfig.getUsage());
			}

		}
	}

	/**
	 * Initializes Cloc.
	 */
	private void init() {
		ClocService.init();
	}

	private void printLimitedRange(final BranchInfo... bis) {

		debugRange();

		for (final BranchInfo bi : bis) {

			final AuthorInfoBuilder aib = bi.getAuthorStatistics().limitToDateRange(
					Util.getAppropriateRange(config.getStart(), config.getEnd()));
			out.println(aib);

		}
	}

	/**
	 * Sets the config parameters. Cannot be null.
	 *
	 * @param config
	 * @return
	 */
	private Application setConfig(final ProgramConfig config) {
		LOGGER.trace("Setting configuration as " + config);
		if (config != null) {
			this.config = config;
		}
		return this;
	}

	static ProgramConfig getConfiguration() {
		return APPLICATION.getConfig();
	}

	static Application getInstance() {
		return APPLICATION;
	}

	public static void main(final String[] args) throws GitAPIException {
		
		Util.setLoggingLevel(Level.INFO);
		
		try {
			APPLICATION.setConfig(ProgramConfig.parseArgs(args)).execute();
		} catch (final Exception e) {
			err.println("An error occurred during application execution: " + e.getMessage());
			LOGGER.debug("Error: ", e);
		}

	}

	static Application setConfiguration(final ProgramConfig config) {
		getInstance().setConfig(config);
		return getInstance();
	}

}
