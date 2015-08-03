package com.cisco.dft.sdk.vcs.main;

import java.io.PrintStream;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import ch.qos.logback.classic.Level;

import com.cisco.dft.sdk.vcs.common.Util;
import com.cisco.dft.sdk.vcs.core.AuthorInfoBuilder;
import com.cisco.dft.sdk.vcs.core.BranchInfo;
import com.cisco.dft.sdk.vcs.core.GitRepo;
import com.cisco.dft.sdk.vcs.core.SVNRepo;
import com.cisco.dft.sdk.vcs.main.ProgramConfig.Action;

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
	 * @throws SVNException
	 */
	void execute() throws GitAPIException, SVNException {

		if (config.isDebugEnabled()) {
			Util.setLoggingLevel(Level.DEBUG);
		}

		LOGGER.debug("Executing with params: " + config.toString());

		if (config.shouldShowVersion()) {
			out.println(VERISION);
		}

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

	/**
	 * Initializes Cloc.
	 */
	private void init() {
		ClocService.init();
	}

	/**
	 * Prints the help message to stdout.
	 */
	public void help() {
		if (config.getUrl() == null) {
			out.println(ProgramConfig.getUsage());
		} else {
			
			try {
				Action action = Action.valueOf(config.getUrl().toUpperCase());
				out.print(action.getUsage());
			} catch (Exception e) {
				LOGGER.trace("Unrecognized help parameter", e);
				out.println(ProgramConfig.getUsage());
			}
			
		}
	}

	/**
	 * Runs with some debug data and preset options and parameters.
	 * 
	 * @throws GitAPIException
	 */
	private void debug() throws GitAPIException {
		// setConfig(ProgramConfig.DEBUG).execute();
		try {
			init();
			Util.setLoggingLevel(Level.DEBUG);
			SVNRepo repo = new SVNRepo(config.getUrl());
			repo.sync(config.getBranch() == null ? SVNRepo.TRUNK : config
					.getBranch());
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

		UsernamePasswordCredentialsProvider cp = new UsernamePasswordCredentialsProvider(config
				.getUsername(), config.getPassword());

		GitRepo repo = new GitRepo(config.getUrl(), config.getBranch(), false, cp);
		repo.sync(config.getBranch(), config.shouldGenerateStats(),
				config.shouldUseCloc());

		if (!(config.getStart() == null && config.getEnd() == null)) {

			if (config.getBranch() != null) {
				printLimitedRange(repo.getRepoStatistics().getBranchInfoFor(
						config.getBranch()));
			} else {
				printLimitedRange(repo.getRepoStatistics().getBranchInfos());
			}

		} else {
			out.println(repo.getRepoStatistics().toString(
					config.shouldShowCommits()));
		}

		repo.close();

	}
	
	/**
	 * Treats the url as a svn repo
	 * 
	 * @throws SVNException
	 */
	private void analyzeAsSVN() throws SVNException {

		SVNRepo repo = new SVNRepo(config.getUrl(), config.getBranch());
		
		out.println(repo.getRepoStatistics());

	}

	private void printLimitedRange(BranchInfo... bis) {

		debugRange();

		for (BranchInfo bi : bis) {

			AuthorInfoBuilder aib = bi.getAuthorStatistics()
					.limitToDateRange(
							Util.getAppropriateRange(config.getStart(),
									config.getEnd()));
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

	public static void main(String[] args) throws GitAPIException {

		try {
			APPLICATION.setConfig(ProgramConfig.parseArgs(args)).execute();
		} catch (Exception e) {
			err.println("An error occurred during application execution: "
					+ e.getMessage());
			LOGGER.debug("Error: ", e);
		}

		// TODO cloc analysis - full testing

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
