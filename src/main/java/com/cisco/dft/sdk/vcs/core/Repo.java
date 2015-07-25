package com.cisco.dft.sdk.vcs.core;

import java.util.List;

public abstract class Repo {
	
	protected final RepoInfo repoInfo = new RepoInfo();
	
	/**
	 * This refreshes the repository and updates any information. Meaning, it should populate
	 * the RepoInfo object with information about the repo.
	 */
	public abstract void sync();

	/**
	 * Returns a list of the branches in this repository.
	 * 
	 * @return
	 */
	public abstract List<String> getBranches();

	/**
	 * Gets the general information about this repository:
	 * <ol>
	 * <li>Lines of code</li>
	 * <li>Number of files</li>
	 * <li>Language statistics</li>
	 * </ol>
	 * Note: Merge requests do not seem to show any additions or deletions.
	 * 
	 * @return a copy of the statistics object. changing this will not effect
	 *         statistics as a whole.
	 */
	public RepoInfo getRepoStatistics() {
		return repoInfo;
	}

	protected static String guessName(String url) {
		String value = url;
		String[] splitten = { "Unkown" };

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

}
