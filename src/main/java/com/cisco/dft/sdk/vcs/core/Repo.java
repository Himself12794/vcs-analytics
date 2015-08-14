package com.cisco.dft.sdk.vcs.core;

import java.io.File;

import com.cisco.dft.sdk.vcs.core.util.CommitLogger;

/**
 * Abstract base class used by all repos.
 * 
 * @author phwhitin
 *
 */
public abstract class Repo {

	public static final String DEFAULT_DIRECTORY_BASE = "vcs-analytics/repositories/";

	protected final RepoInfo repoInfo = new RepoInfo();
	
	protected File theDirectory;

	/**
	 * Gets all generated data for this repository. Make sure
	 * {@link Repo#sync()} is called to ensure this information is up to date.
	 *
	 * @return a copy of the statistics object
	 */
	public RepoInfo getRepoStatistics() {
		return repoInfo;
	}

	/**
	 * This refreshes the repository and updates any information. Meaning, it
	 * should populate the RepoInfo object with information about the repo.
	 */
	public abstract void sync();

	public static String guessName(final String url) {
		String value = url;
		String[] splitten = { "Unknown" };

		if (value.endsWith(".git")) {
			value = value.replace(".git", "");
		}
		
		if (value.contains("/")) {
			splitten = value.split("/");
		} else if (value.contains("\\")) {
			splitten = value.split("\\\\");
		}

		return splitten[splitten.length - 1];
	}
	
	protected CommitLogger getCommitLogger() {
		return new CommitLogger(theDirectory);
	}
	
	public String toString() {
		return repoInfo.toString();
	}

}
