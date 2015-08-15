package com.pwhiting.sdk.vcs.main;

import java.util.Date;

import org.tmatesoft.svn.core.wc.SVNRevision;

import com.pwhiting.util.ArgParser;
import com.pwhiting.util.Util;

/**
 * Simple pojo used to define Application config and context.
 *
 * @author phwhitin
 *
 */
public class ProgramConfig {
	
	static final ProgramConfigMapper MAPPER = new ProgramConfigMapper();

	/** Pre-set config for an INIT action. Useful for testing */
	static final ProgramConfig INIT = ProgramConfig.parseArgs("init", "--nostats",
			"--builtin-analysis");

	static final ProgramConfig HELP = ProgramConfig.parseArgs("help");

	/** A debug test configuration */
	static final ProgramConfig DEBUG = ProgramConfig.parseArgs("analyze", "-d",
			"https://github.com/Himself12794/Heroes-Mod", "-s", "--branch=branches/develop");

	/**
	 * Just like {@link ProgramConfig.DEBUG}, but with debug logging off for
	 * unit tests
	 */
	static final ProgramConfig TEST = ProgramConfig.parseArgs("analyze",
			"https://github.com/Himself12794/powersAPI.git", "--branch=master",
			"--start=1234567891000", "--end=1234867891000", "--no-lang-stats");

	static final ProgramConfig DEFAULT = HELP;

	private final Action action;

	private final String url;

	private final String branch;

	private final String username;

	private final String password;

	private final boolean shouldGenerateStats;

	private final boolean useCloc;
	
	boolean shouldGenerateLangStats;

	boolean debug;

	boolean showVersion;

	boolean noCommits;

	boolean forceGit;

	boolean forceSvn;
	
    boolean svnSkipNonSourceCodeFiles;
	
	SVNRevision revA;
	
	SVNRevision revB;

	Date start;

	Date end;

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
	
	public boolean shouldSvnSkipNonSourceCodeFiles() {
		return svnSkipNonSourceCodeFiles;
	}

	public SVNRevision getRevA() {
		return revA;
	}

	public SVNRevision getRevB() {
		return revB;
	}

	public boolean shouldGetLangStats() {
		return shouldGenerateLangStats;
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
		return Util.toString(this);
	}

	public static String getUsage() {
		final StringBuilder value = new StringBuilder("This application runs an analysis on remote repositories and prints the results.\n");
		value.append("\nCommands:");
		
		for (Action action : Action.values()) {
			value.append(action.getUsage());
			value.append("\n");
		}
			
		value.append("\nUniversal options");
		value.append("\n  -d    - Enable debug logging");
		value.append("\n  -v    - Shows version");
		value.append("\n");
		return value.toString();
	}

	public static ProgramConfig parseArgs(final String... args) {
		return ArgParser.parseArgs(MAPPER, args);
	}
	
	public static ProgramConfig parseArgs(final String arg) {
		return parseArgs(arg.split(" "));
	}

}
