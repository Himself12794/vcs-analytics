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
public class AuthorInfo extends RecursiveDateLimitedDataContainer<AuthorCommit> {

	private final String name;

	private int additions, deletions;

	private int limitedAdditions, limitedDeletions;

	AuthorInfo(final String name) {
		this(name, 0, 0, new ArrayList<AuthorCommit>());
	}

	AuthorInfo(final String name, final int additions, final int deletions,
			final List<AuthorCommit> commits) {
		super(commits);
		this.name = name;
		this.additions = additions;
		this.deletions = deletions;

	}

	@Override
	public boolean add(final AuthorCommit ac) {

		for (final AuthorCommit a : data) {
			if (a.getTimestamp().equals(ac.getTimestamp())) { return false; }
		}

		return super.add(ac);
	}

	@Override
	public AuthorInfo copy() {
		final AuthorInfo theCopy = new AuthorInfo(name, additions, deletions, data);
		theCopy.limitToDateRange(getDateRange());
		return theCopy;
	}

	public int getAdditions() {
		return isLimited() ? limitedAdditions : additions;
	}

	public AuthorCommit getCommitById(final String id) {

		for (final AuthorCommit ac : data) {
			if (ac.getId().equals(id)) { return ac; }
		}

		throw new CommitNotFoundException();
	}

	public int getCommitCount() {
		return isLimited() ? limitedData.size() : data.size();
	}

	/**
	 * Gets a list of commits this author has made. Use
	 * {@link AuthorInfo#limitToDateRange(Date, Date, boolean)} to set the
	 * range.
	 *
	 * @return list of author commits
	 */
	public List<AuthorCommit> getCommits() {

		final List<AuthorCommit> toUse = getData();

		final Comparator<AuthorCommit> sorter = new Comparator<AuthorCommit>() {

			@Override
			public int compare(final AuthorCommit p1, final AuthorCommit p2) {

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

	@Override
	public AuthorInfo includeAll() {
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

		for (final AuthorCommit ac : limitedData) {
			limitedAdditions += ac.getAdditions();
			limitedDeletions += ac.getDeletions();
		}
	}

	@Override
	public String toString() {

		final StringBuilder value = new StringBuilder();

		value.append("Name: " + name + ", ");
		value.append("Commits: " + getCommitCount() + ", ");
		value.append("Additions: " + getAdditions() + ", ");
		value.append("Deletions: ");
		value.append(getDeletions());
		value.append("\n");

		for (final AuthorCommit ac : getCommits()) {
			value.append(" - " + ac.toString() + "\n\n");
		}

		value.append("\n");

		return value.toString();
	}

}
