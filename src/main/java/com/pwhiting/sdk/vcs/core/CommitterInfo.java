package com.pwhiting.sdk.vcs.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.pwhiting.sdk.vcs.core.error.CommitNotFoundException;
import com.pwhiting.util.DateLimitedDataContainer.RecursiveDateLimitedDataContainer;

/**
 * Wrapper class used for author data pulled from the repository.
 *
 * @author phwhitin
 *
 */
public class CommitterInfo extends RecursiveDateLimitedDataContainer<Commit> {

	private final String committer;
	
	private final String committerEmail;
	
	private final String author;
	
	private final String authorEmail;

	private int additions, deletions;

	private int limitedAdditions, limitedDeletions;

	CommitterInfo(final String name, final String email, final String author, final String authorEmail) {
		this(name, email, author, authorEmail, 0, 0, new ArrayList<Commit>());
	}

	CommitterInfo(final String committer, final String committerEmail, final String author, final String authorEmail, final int additions, final int deletions,
			final List<Commit> commits) {
		super(commits);
		this.committer = committer;
		this.committerEmail = committerEmail;
		this.author = author;
		this.authorEmail = authorEmail;
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
		final CommitterInfo theCopy = new CommitterInfo(committer, committerEmail, author, authorEmail, additions, deletions, data);
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

	public String getCommitterName() {
		return committer;
	}
	
	public String getCommitterEmail() {
		return committerEmail;
	}
	
	public String getAuthor() {
		return author;
	}

	public String getAuthorEmail() {
		return authorEmail;
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

		value.append("Name: " + committer + ", ");
		value.append("Email: " + committerEmail + ", ");
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
