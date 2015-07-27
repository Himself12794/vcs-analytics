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
	 * This is vital so users know which branches exist,
	 * so information for specific branches can be accessed.
	 * 
	 * @return
	 */
	public abstract List<String> getBranches();

	/**
	 * Gets all generated data for this repository.
	 * Make sure {@link Repo#sync()} is called to ensure
	 * this information is up to date.
	 * 
	 * @return a copy of the statistics object
	 */
	public RepoInfo getRepoStatistics() {
		return repoInfo;
	}

	public static String guessName(String url) {
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
