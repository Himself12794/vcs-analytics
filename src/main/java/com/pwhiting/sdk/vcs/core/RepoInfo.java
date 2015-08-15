package com.pwhiting.sdk.vcs.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.google.common.collect.Maps;
import com.pwhiting.sdk.vcs.core.error.BranchNotFoundException;
import com.pwhiting.sdk.vcs.util.Util;

/**
 * Data object that holds all information for a repo.
 * 
 * @author phwhitin
 *
 */
public class RepoInfo {

	private String name;

	private Repo theRepo;

	private final Map<String, BranchInfo> branches = Maps.newHashMap();

	RepoInfo() {
		this("Unknown");
	}

	RepoInfo(final String name) {
		this.name = name;
	}

	/**
	 * Checks if the branch exists.
	 *
	 * @param branch
	 * @return
	 */
	public boolean branchExists(final String branch) {

		for (BranchInfo bi : branches.values()) {
			if (bi.getBranch().equals(branch)) { return true; }
		}

		return false;

	}

	/**
	 * Gets an array of all the branches for which this repo has information.
	 *
	 * @return
	 */
	public String[] getBranches() {
		return branches.keySet().toArray(new String[branches.keySet().size()]);
	}

	/**
	 * If branch info by the given name does not exist, creates and returns it.
	 *
	 * @param branch
	 * @return
	 */
	BranchInfo getBranchInfo(final String branch) {

		BranchInfo bi;

		if (!branches.containsKey(branch)) {

			bi = new BranchInfo(branch, theRepo);
			branches.put(branch, bi);

		} else {
			bi = branches.get(branch);
		}

		return bi;

	}

	/**
	 * Get the information for a specific branch. To find what branches the repo
	 * has, use {@link RepoInfo#getBranches()}. The "branch" is the path in the
	 * repository, so a branch could possibly be "branches/develop."
	 *
	 * @param branch
	 *            for which to get information
	 * @return Information on the branch, or empty information if the branch
	 *         does not exist. This is never null.
	 * @throws BranchNotFoundException
	 */
	public BranchInfo getBranchInfoFor(final String branch) throws BranchNotFoundException {

		final String resolved = (theRepo instanceof GitRepo) ? BranchInfo
				.branchNameResolver(branch) : branch;

		if (branches.containsKey(resolved)) {
			return branches.get(resolved);
		} else {
			throw new BranchNotFoundException("Branch " + branch
					+ " does not exist, or has not been synced");
		}

	}

	public BranchInfo[] getBranchInfos() {
		return branches.values().toArray(new BranchInfo[branches.values().size()]);
	}

	/**
	 * Gets the repo name as guessed from the url
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Removes data for branches that no longer exist.
	 *
	 * @param branches
	 */
	void resolveBranchInfo(final List<String> branches) {

		for (final String branch : this.branches.keySet()) {

			if (!branches.contains(branch)) {
				this.branches.remove(branch);
			}

		}

	}

	void setName(final String name) {
		this.name = name;
	}

	void setRepo(final Repo theRepo) {
		this.theRepo = theRepo;
	}

	@Override
	public String toString() {
		return toString(true);
	}

	public String toString(final boolean showCommits) {

		final int length = 40;
		final Date date = new Date(System.currentTimeMillis());
		final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
		final String formattedDate = sdf.format(date);

		final StringBuilder value = new StringBuilder();

		value.append(Util.getNTimes('+', length, true));
		value.append("Report for repo: ");
		value.append(getName());
		value.append("\nTime of report: ");
		value.append(formattedDate);
		value.append("\n");
		value.append("Branches in this report: ");

		for (final String branch : branches.keySet()) {
			value.append(BranchInfo.branchTrimmer(branch));
			value.append(", ");
		}

		value.append("\n");
		value.append(Util.getNTimes('-', length, true));

		for (final BranchInfo bi : branches.values()) {
			value.append(bi.toString(showCommits));
			value.append(Util.getNTimes('*', length, true));
		}

		value.append(Util.getNTimes('=', length, true));

		return value.toString();

	}

}
