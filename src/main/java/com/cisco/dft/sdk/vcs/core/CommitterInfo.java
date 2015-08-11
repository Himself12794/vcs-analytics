package com.cisco.dft.sdk.vcs.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.cisco.dft.sdk.vcs.core.error.CommitNotFoundException;
import com.cisco.dft.sdk.vcs.util.DateLimitedDataContainer.RecursiveDateLimitedDataContainer;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

/**
 * Wrapper class used for author data pulled from the repository.
 *
 * @author phwhitin
 *
 */
public class CommitterInfo extends RecursiveDateLimitedDataContainer<Commit> {

	private final String name;
	
	private final String email;

	private int additions, deletions;

	private int limitedAdditions, limitedDeletions;

	CommitterInfo(final String name, final String email) {
		this(name, email, 0, 0, new ArrayList<Commit>());
	}

	CommitterInfo(final String name, final String email, final int additions, final int deletions,
			final List<Commit> commits) {
		super(commits);
		this.name = name;
		this.email = email;
		this.additions = additions;
		this.deletions = deletions;

	}

	@Override
	public boolean add(final Commit ac) {

		for (final Commit a : data) {
			if (a.getTimestamp().equals(ac.getTimestamp())) { return false; }
		}

		return super.add(ac);
	}

	@Override
	public CommitterInfo copy() {
		final CommitterInfo theCopy = new CommitterInfo(name, email, additions, deletions, data);
		theCopy.limitToDateRange(getDateRange());
		return theCopy;
	}

	public int getAdditions() {
		return isLimited() ? limitedAdditions : additions;
	}

	public Commit getCommitById(final String id) {

		for (final Commit ac : data) {
			if (ac.getId().equals(id)) { return ac; }
		}

		throw new CommitNotFoundException();
	}

	public int getCommitCount() {
		return isLimited() ? limitedData.size() : data.size();
	}

	/**
	 * Gets a list of commits this author has made. Use
	 * {@link CommitterInfo#limitToDateRange(Date, Date, boolean)} to set the
	 * range.
	 *
	 * @return list of author commits
	 */
	public List<Commit> getCommits() {

		final List<Commit> toUse = getData();

		final Comparator<Commit> sorter = new Comparator<Commit>() {

			@Override
			public int compare(final Commit p1, final Commit p2) {

				return p2.getTimestamp().compareTo(p1.getTimestamp());
			}

		};

		Collections.sort(toUse, sorter);

		return Lists.newArrayList(toUse);
	}

	public int getDeletions() {
		return isLimited() ? limitedDeletions : deletions;
	}

	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}

	@Override
	public CommitterInfo includeAll() {
		super.includeAll();
		limitedAdditions = 0;
		limitedDeletions = 0;

		return this;
	}

	void incrementAdditions(final int x) {
		additions += x;
	}

	void incrementDeletions(final int x) {
		deletions += x;
	}

	@Override
	public void limitToDateRange(final Range<Date> dateRange) {
		includeAll();
		super.limitToDateRange(dateRange);

		for (final Commit ac : limitedData) {
			limitedAdditions += ac.getAdditions();
			limitedDeletions += ac.getDeletions();
		}
	}

	@Override
	public String toString() {

		final StringBuilder value = new StringBuilder();

		value.append("Name: " + name + ", ");
		value.append("Email: " + email + ", ");
		value.append("Commits: " + getCommitCount() + ", ");
		value.append("Additions: " + getAdditions() + ", ");
		value.append("Deletions: ");
		value.append(getDeletions());
		value.append("\n");

		for (final Commit ac : getCommits()) {
			value.append(" - " + ac.toString() + "\n\n");
		}

		value.append("\n");

		return value.toString();
	}

}
