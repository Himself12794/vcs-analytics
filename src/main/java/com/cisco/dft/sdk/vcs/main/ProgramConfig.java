package com.cisco.dft.sdk.vcs.main;

import java.util.Map;

import com.cisco.dft.sdk.vcs.common.Util;

public class ProgramConfig {
	
	private final Action action;
	
	@Required
	private final String url;
	
	@Default("master")
	private final String branch;
	
	private final boolean shouldGenerateStats;
	
	private final boolean useCloc;
	
	ProgramConfig(Action action, String url, String branch, boolean value, boolean value2) {
		this.action = action;
		this.url = url;
		this.branch = branch;
		this.shouldGenerateStats = value;
		this.useCloc = value2;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getBranch() {
		return branch;
	}
	
	public Action getAction() {
		return action;
	}
	
	public enum Action {
		ANALYZE, HELP, INIT
	}
	
	public boolean shouldGenerateStats() {
		return this.shouldGenerateStats;
	}
	
	public boolean shouldUseCloc() {
		return this.useCloc;
	}
	
	public static ProgramConfig parseArgMap(Map<String, String> args2) {
		
		String branch = Util.getOrDefault(args2, "branch", null);
		boolean generateStats = !args2.containsKey("nostats");
		boolean useCloc = !args2.containsKey("builtin-analysis");
		String url = Util.getOrDefault(args2, "url", null);
		Action action = Action.valueOf(Util.getOrDefault(args2, "action", "HELP"));
		return new ProgramConfig(action, url, branch, generateStats, useCloc);
	}
	
	@Override
	public String toString() {
		String value = "Action=" + action.name()
						+ ",Url=" + url
						+ ",Branch=" + branch
						+ ",GenerateStats=" + shouldGenerateStats
						+ ",UseCloc=" + useCloc;
		return value;
	}
	
	public static String getUsage() {
		String value = 
				"Commands:"
			  + "\n  analyze - Gives statistics for a Git repository"
			  + "\n      --url=<url> (required)"
			  + "\n      --branch=<branch> (defaults to master)"
			  + "\n      --nostatistics (just clones the repo)"
			  + "\n      --builtin-analysis (Uses builtin process, no CLOC required)"
			  + "\n"
			  + "\n  help - Shows help"
			  + "\n"
			  + "\n  init - Extracts CLOC resources";
		return value;
	}

}
