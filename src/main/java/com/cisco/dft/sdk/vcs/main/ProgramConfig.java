package com.cisco.dft.sdk.vcs.main;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple pojo used to define Application config and context.
 *
 * @author phwhitin
 *
 */
public class ProgramConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger("ProgamConfig");

	/** Pre-set config for an INIT action. Useful for testing */
	static final ProgramConfig INIT = ProgramConfig.parseArgs("init", "--nostats",
			"--builtin-analysis");

	static final ProgramConfig HELP = ProgramConfig.parseArgs("help");

	/** A debug test configuration */
	static final ProgramConfig DEBUG = ProgramConfig.parseArgs("analyze", "-d",
			"https://github.com/Himself12794/Heroes-Mod.git", "-s", "--branch=trunk");

	/**
	 * Just like {@link ProgramConfig.DEBUG}, but with debug logging off for
	 * unit tests
	 */
	static final ProgramConfig TEST = ProgramConfig.parseArgs("analyze",
			"https://github.com/Himself12794/powersAPI.git", "--branch=master",
			"--start=1234567891000", "--end=1234867891000");

	static final ProgramConfig DEFAULT = HELP;

	private final Action action;

	private final String url;

	private final String branch;

	private final String username;

	private final String password;

	private final boolean shouldGenerateStats;

	private final boolean useCloc;

	private boolean debug;

	private boolean showVersion;

	private boolean noCommits;

	private boolean forceGit;

	private boolean forceSvn;

	private Date start;

	private Date end;

	ProgramConfig(final Action action, final String url, final String branch,
			final String username, final String password, final boolean generateStats,
			final boolean useCloc) {
		this.action = action;
		this.url = url;
		this.branch = branch;
		this.username = username;
		this.password = password;
		shouldGenerateStats = generateStats;
		this.useCloc = useCloc;
	}

	public Action getAction() {
		return action;
	}

	public String getBranch() {
		return branch;
	}

	public Date getEnd() {
		return end != null ? (Date) end.clone() : null;
	}

	String getPassword() {
		return password;
	}

	public Date getStart() {
		return start != null ? (Date) start.clone() : null;
	}

	public String getUrl() {
		return url;
	}

	String getUsername() {
		return username;
	}

	public boolean isDebugEnabled() {
		return debug;
	}

	public boolean shouldForceGit() {
		return forceGit;
	}

	public boolean shouldForceSvn() {
		return forceSvn;
	}

	public boolean shouldGenerateStats() {
		return shouldGenerateStats;
	}

	public boolean shouldShowCommits() {
		return !noCommits;
	}

	public boolean shouldShowVersion() {
		return showVersion;
	}

	public boolean shouldUseCloc() {
		return useCloc;
	}

	@Override
	public String toString() {
		final String value = "Action=" + action.name() + ",Url=" + url + ",Branch=" + branch
				+ ",GenerateStats=" + shouldGenerateStats + ",UseCloc=" + useCloc + ",Start="
				+ (start == null ? "first-commit" : start) + ",End="
				+ (end == null ? "last-commit" : end) + ",Debug=" + debug;
		return value;
	}

	private static Date getDate(final String date) {

		try {
			return DateTime.parse(date).toDateTime(DateTimeZone.getDefault()).toDate();
		} catch (final Exception iae) {
			LOGGER.trace("Could not read start input as a DateTime object", iae);
		}

		try {
			return new Date(Long.parseLong(date));
		} catch (final NumberFormatException nfe) {
			// No action needed
		}

		return null;

	}

	public static String getUsage() {
		final String value = "This application runs an analysis on remote repositories and prints the results.\n"
				+ "\nCommands:"
				+ "\n  analyze <url> - Gives statistics for a Git repository"
				+ "\n      --branch=<branch> (Limits statistics to specified branch)"
				+ "\n      --nostatistics (Indicates to just clone the repo)"
				+ "\n      --builtin-analysis (Indicates to use builtin stat analysis)"
				+ "\n      --username=<username> (Used for access to private repos)"
				+ "\n      --password=<password> (Used for access to private repos)"
				+ "\n      --start=<epoch-time> (Format: YYYY-MM-DDTHH:MM:SS+HH:MM)"
				+ "\n      --end=<epoch-time> (Format: YYYY-MM-DDTHH:MM:SS+HH:MM)"
				+ "\n      --nocommits (Indicates that only language information should be shown)"
				+ "\n      -s (forces the application to treat the url as a SVN repo"
				+ "\n      -g (forces the application to treat the url as a Git repo"
				+ "\n"
				+ "\n  help  - Shows help"
				+ "\n"
				+ "\n  init  - Extracts CLOC resources"
				+ "\n"
				+ "\n  debug - runs the program with some debug data"
				+ "\n  -d    - Enable debug logging" + "\n  -v    - Shows version\n";
		return value;
	}

	public static ProgramConfig parseArgs(final ArgParser parser) {

		Action action = parser.getActionAsEnum(Action.class);

		if (action == null) {
			action = Action.HELP;
		}

		final String url = parser.getActionParameterByIndex(0);
		final String branch = parser.getString("branch");
		final String username = parser.getString("username", "");
		final String password = parser.getString("password", "");
		final boolean generateStats = !parser.getBoolean("nostats");
		final boolean useCloc = !parser.getBoolean("builtin-analysis");
		final boolean version = parser.getBoolean("v");
		final boolean debug = parser.getBoolean("d");
		final boolean noCommits = parser.getBoolean("nocommits");
		final boolean forceGit = parser.getBoolean("forceGit") || parser.getBoolean("g");
		final boolean forceSvn = parser.getBoolean("forceSvn") || parser.getBoolean("s");
		final Date end = getDate(parser.getString("end"));
		final Date start = getDate(parser.getString("start"));

		final ProgramConfig config = new ProgramConfig(action, url, branch, username, password, generateStats, useCloc);
		config.end = end;
		config.start = start;
		config.debug = debug;
		config.showVersion = version;
		config.noCommits = noCommits;
		config.forceGit = forceGit;
		config.forceSvn = forceSvn;

		return config;
	}

	public static ProgramConfig parseArgs(final String... args) {
		return parseArgs(ArgParser.parse(args));
	}
	
	public static ProgramConfig parseArgs(final String arg) {
		return parseArgs(arg.split(" "));
	}

}
