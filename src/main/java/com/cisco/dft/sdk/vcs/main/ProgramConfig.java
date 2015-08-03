package com.cisco.dft.sdk.vcs.main;

import java.util.Date;

import com.google.common.base.Predicate;

/**
 * Simple pojo used to define Application config and context.
 * 
 * @author phwhitin
 *
 */
public class ProgramConfig {

	/** Pre-set config for an INIT action. Useful for testing */
	static final ProgramConfig INIT = ProgramConfig.parseArgs("init",
			"--nostats", "--builtin-analysis");

	static final ProgramConfig HELP = ProgramConfig.parseArgs("help");

	/** A debug test configuration */
	static final ProgramConfig DEBUG = ProgramConfig.parseArgs("analyze", "-d",
			"https://github.com/Himself12794/powersAPI.git", "--branch=master",
			"--nocommits");

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

	ProgramConfig(Action action, String url, String branch, String username,
			String password, boolean generateStats, boolean useCloc) {
		this.action = action;
		this.url = url;
		this.branch = branch;
		this.username = username;
		this.password = password;
		this.shouldGenerateStats = generateStats;
		this.useCloc = useCloc;
	}

	public boolean shouldForceGit() {
		return forceGit;
	}

	public boolean shouldForceSvn() {
		return forceSvn;
	}

	public boolean shouldShowCommits() {
		return !noCommits;
	}

	public boolean shouldShowVersion() {
		return showVersion;
	}

	public boolean isDebugEnabled() {
		return debug;
	}

	public Date getStart() {
		return start != null ? (Date) start.clone() : null;
	}

	public Date getEnd() {
		return end != null ? (Date) end.clone() : null;
	}

	public String getUrl() {
		return url;
	}

	public String getBranch() {
		return branch;
	}

	String getUsername() {
		return username;
	}

	String getPassword() {
		return password;
	}

	public Action getAction() {
		return action;
	}

	public boolean shouldGenerateStats() {
		return this.shouldGenerateStats;
	}

	public boolean shouldUseCloc() {
		return this.useCloc;
	}

	public static ProgramConfig parseArgs(ArgParser parser) {

		final Action action = parser.getActionAsEnum(Action.class);

		if (action == null) { throw new IllegalArgumentException("Command "
				+ parser.getAction() + " not recognized"); }

		final String url = parser.getActionParameterByIndex(0);
		final String branch = parser.getString("branch");
		final String username = parser.getString("username", "");
		final String password = parser.getString("password", "");
		final boolean generateStats = !parser.getBoolean("nostats");
		final boolean useCloc = !parser.getBoolean("builtin-analysis");
		final boolean version = parser.getBoolean("v");
		final boolean debug = parser.getBoolean("d");
		final boolean noCommits = parser.getBoolean("nocommits");
		final boolean forceGit = parser.getBoolean("forceGit")
				|| parser.getBoolean("g");
		final boolean forceSvn = parser.getBoolean("forceSvn")
				|| parser.getBoolean("s");
		final Date start = parser.getLongAsType("start", Date.class);
		final Date end = parser.getLongAsType("end", Date.class);

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

	public static ProgramConfig parseArgs(String... args) {
		return parseArgs(ArgParser.parse(args));
	}

	public static String getUsage() {
		String value = "This application runs an analysis on remote repositories and prints the results.\n"
				+ "\nCommands:"
				+ "\n  analyze <url> - Gives statistics for a Git repository"
				+ "\n      --branch=<branch> (Limits statistics to specified branch)"
				+ "\n      --nostatistics (Indicates to just clone the repo)"
				+ "\n      --builtin-analysis (Indicates to use builtin stat analysis)"
				+ "\n      --username=<username> (Used for access to private repos)"
				+ "\n      --password=<password> (Used for access to private repos)"
				+ "\n      --start=<epoch-time> (Epoch time, in millis, to limit start date)"
				+ "\n      --end=<epoch-time> (Epoch time, in millis, to limit end date)"
				+ "\n      --nocommits (Indicates that only language information should be shown)"
				+ "\n"
				+ "\n  help  - Shows help"
				+ "\n"
				+ "\n  init  - Extracts CLOC resources"
				+ "\n"
				+ "\n  debug - runs the program with some debug data"
				+ "\n  -d    - Enable debug logging"
				+ "\n  -v    - Shows version\n";
		return value;
	}

	@Override
	public String toString() {
		final String value = "Action=" + action.name() + ",Url=" + url
				+ ",Branch=" + branch + ",GenerateStats=" + shouldGenerateStats
				+ ",UseCloc=" + useCloc + ",Start="
				+ (start == null ? "first-commit" : start) + ",End="
				+ (end == null ? "last-commit" : end) + ",Debug=" + debug;
		return value;
	}

	/** Actions the application can perform */
	public static enum Action {

		ANALYZE("Gets statistics for a remote repo. Usage: analyze <url>", new Predicate<ProgramConfig>() {

			@Override
			public boolean apply(ProgramConfig input) {
				return input == null ? false : input.url != null;
			}

		}), HELP("Shows usage for an action."), INIT("Does a test initialization of cloc"), DEBUG("Runs with preset debug params");

		private final String usage;

		private final Predicate<ProgramConfig> validity;

		Action(String usage) {
			this(usage, new Predicate<ProgramConfig>() {

				@Override
				public boolean apply(ProgramConfig input) {
					return true;
				}

			});
		}

		Action(String usage, Predicate<ProgramConfig> requirements) {
			this.usage = usage;
			this.validity = requirements;
		}

		public String getUsage() {
			return usage;
		}

		/**
		 * Checks whether or not the command is valid using the given
		 * configuration
		 * 
		 * @param params
		 * @return
		 */
		public boolean isValid(ProgramConfig params) {
			return validity.apply(params);
		}
	}

}
