package com.cisco.dft.sdk.vcs.main;

import java.util.Date;

public class ProgramConfig {

	public static enum Action {
		ANALYZE, HELP, INIT, DEBUG
	}

	static final ProgramConfig INIT = ProgramConfig.parseArgs("init");

	static final ProgramConfig HELP = ProgramConfig.parseArgs("help");

	/** A debug test configuration */
	static final ProgramConfig DEBUG = ProgramConfig.parseArgs("analyze", "-d",
			"https://github.com/Himself12794/powersAPI.git", "--branch=master", "--nocommits");

	/**
	 * Just like {@link ProgramConfig.DEBUG}, but with debug logging off for
	 * unit tests
	 */
	static final ProgramConfig TEST = ProgramConfig.parseArgs("analyze",
			"https://github.com/Himself12794/powersAPI.git", "--branch=master");

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

		final String url = parser.getActionParameter();
		final String branch = parser.getString("branch");
		final String username = parser.getString("username", "");
		final String password = parser.getString("password", "");
		final boolean generateStats = !parser.getBoolean("nostats");
		final boolean useCloc = !parser.getBoolean("builtin-analysis");
		final boolean version = parser.getBoolean("v");
		final boolean debug = parser.getBoolean("d");
		final boolean noCommits = parser.getBoolean("nocommits");
		final Date start = parser.getLongAsType("start", Date.class);
		final Date end = parser.getLongAsType("end", Date.class);

		final ProgramConfig config = new ProgramConfig(action, url, branch, username, password, generateStats, useCloc);
		config.end = end;
		config.start = start;
		config.debug = debug;
		config.showVersion = version;
		config.noCommits = noCommits;

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

}
