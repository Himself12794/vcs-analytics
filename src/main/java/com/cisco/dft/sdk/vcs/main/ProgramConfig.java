package com.cisco.dft.sdk.vcs.main;

import java.util.Date;
import java.util.Map;

import com.cisco.dft.sdk.vcs.common.Util;

public class ProgramConfig {

	static final ProgramConfig INIT = ProgramConfig.parseArgMap("init");

	static final ProgramConfig HELP = ProgramConfig.parseArgMap("help");

	static final ProgramConfig DEBUG = ProgramConfig.parseArgMap("analyze",
			"-d", "--url=https://github.com/Himself12794/powersAPI.git",
			"--branch=master");
	
	static final ProgramConfig DEFAULT = HELP;

	private final Action action;

	private final String url;

	private final String branch;

	private final String username;

	private final String password;

	private final boolean shouldGenerateStats;

	private final boolean useCloc;

	private boolean debug;

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

	public boolean isDebugEnabled() {
		return debug;
	}

	void setDebugEnabled(boolean value) {
		debug = value;
	}

	public Date getStart() {
		return start != null ? (Date) start.clone() : null;
	}

	void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end != null ? (Date) end.clone() : null;
	}

	void setEnd(Date end) {
		this.end = end;
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

	public enum Action {
		ANALYZE, HELP, INIT, DEBUG
	}

	public boolean shouldGenerateStats() {
		return this.shouldGenerateStats;
	}

	public boolean shouldUseCloc() {
		return this.useCloc;
	}

	public static ProgramConfig parseArgMap(String... args) {
		Map<String, String> map = ArgParser.getArgMap(args);

		Action action = null;
		String actionStr = (String) Util.getOrDefault(map, "action", "HELP",
				true);

		for (Action act : Action.values()) {
			if (act.name().equals(actionStr)) {
				action = act;
				break;
			}
		}

		if (action == null) {
			System.err.println("Command " + actionStr + " not recognized");
			System.exit(1);
		}

		final String url = (String) Util.getOrDefault(map, "url", null);
		final String branch = (String) Util.getOrDefault(map, "branch", null);
		final String username = (String) Util.getOrDefault(map, "username", "");
		final String password = (String) Util.getOrDefault(map, "password", "");
		final boolean generateStats = !(Boolean.parseBoolean((String) Util
				.getOrDefault(map, "nostats", Boolean.FALSE.toString())));
		final boolean useCloc = !(Boolean
				.parseBoolean((String) Util.getOrDefault(map,
						"builtin-analysis", Boolean.FALSE.toString())));
		final boolean debug = Boolean.parseBoolean((String) Util.getOrDefault(
				map, "d", Boolean.FALSE.toString()));
		Date start = null;

		try {
			start = new Date(Long.valueOf(Util.getOrDefault(map, "start", null)));
		} catch (NumberFormatException e) {
			// Don't need to do anything
		}

		Date end = null;

		try {
			end = new Date(Long.valueOf(Util.getOrDefault(map, "end", null)));
		} catch (NumberFormatException e) {
			// Don't need to do anything
		}

		ProgramConfig config = new ProgramConfig(action, url, branch, username, password, generateStats, useCloc);
		config.setEnd(end);
		config.setStart(start);
		config.setDebugEnabled(debug);

		return config;
	}

	@Override
	public String toString() {
		String value = "Action=" + action.name() + ",Url=" + url + ",Branch="
				+ branch + ",GenerateStats=" + shouldGenerateStats
				+ ",UseCloc=" + useCloc + ",Start="
				+ (start == null ? "first-commit" : start) + ",End="
				+ (end == null ? "last-commit" : end) + ",Debug=" + debug;
		return value;
	}

	public static String getUsage() {
		String value = "This application runs an analysis on remote repositories and prints the results.\n"
				+ "\nCommands:"
				+ "\n  analyze - Gives statistics for a Git repository"
				+ "\n      --url=<url> (required)"
				+ "\n      --branch=<branch> (Omit to run on all branches)"
				+ "\n      --nostatistics (Indicates to just clone the repo)"
				+ "\n      --builtin-analysis (Indicates to use builtin stat analysis)"
				+ "\n      --username=<username> (Used for access to private repos)"
				+ "\n      --password=<password> (Used for access to private repos)"
				+ "\n      --start=<epoch-time> (Epoch time, in millis, to limit start date)"
				+ "\n      --end=<epoch-time> (Epoch time, in millis, to limit end date)"
				+ "\n"
				+ "\n  help - Shows help"
				+ "\n"
				+ "\n  init - Extracts CLOC resources"
				+ "\n"
				+ "\n  debug - runs the program with some debug data"
				+ "\n  -d   - Enable debug logging" + "\n";
		return value;
	}

}
