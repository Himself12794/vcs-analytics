package com.cisco.dft.sdk.vcs.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.cisco.dft.sdk.vcs.common.CommitNotFoundException;
import com.cisco.dft.sdk.vcs.common.DateLimitedDataContainer.RecursiveDateLimitedDataContainer;
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

	AuthorInfo(final String name, int additions, int deletions, List<AuthorCommit> commits) {
		super(commits);
		this.name = name;
		this.additions = additions;
		this.deletions = deletions;

	}

	@Override
	public void limitToDateRange(Range<Date> dateRange) {
		includeAll();
		super.limitToDateRange(dateRange);

		for (AuthorCommit ac : limitedData) {
			limitedAdditions += ac.getAdditions();
			limitedDeletions += ac.getDeletions();
		}
	}

	@Override
	public AuthorInfo includeAll() {
		super.includeAll();
		limitedAdditions = 0;
		limitedDeletions = 0;

		return this;
	}
	
	@Override
	public boolean add(AuthorCommit ac) {
		
		for (AuthorCommit a : this.data) {
			if (a.getTimestamp().equals(ac.getTimestamp())) {
				return false;
			}
		}
		
		return super.add(ac);
	}

	/**
	 * Gets a list of commits this author has made. Use
	 * {@link AuthorInfo#limitToDateRange(Date, Date, boolean)} to set the range.
	 * 
	 * @return list of author commits
	 */
	public List<AuthorCommit> getCommits() {

		List<AuthorCommit> toUse = getData();

		Comparator<AuthorCommit> sorter = new Comparator<AuthorCommit>() {

			@Override
			public int compare(AuthorCommit p1, AuthorCommit p2) {

				return p2.getTimestamp().compareTo(p1.getTimestamp());
			}

		};

		Collections.sort(toUse, sorter);

		return Lists.newArrayList(toUse);
	}

	public String getName() {
		return name;
	}

	public int getCommitCount() {
		return isLimited() ? limitedData.size() : data.size();
	}

	public int getAdditions() {
		return isLimited() ? limitedAdditions : additions;
	}

	void incrementAdditions(int x) {
		this.additions += x;
	}

	public int getDeletions() {
		return isLimited() ? limitedDeletions : deletions;
	}

	void incrementDeletions(int x) {
		deletions += x;
	}
	
	public AuthorCommit getCommitById(String id) {
		
		for (AuthorCommit ac : data) {
			if (ac.getId().equals(id)) { return ac; }
		}
		
		throw new CommitNotFoundException();
	}
	
	public AuthorInfo copy() {
		AuthorInfo theCopy = new AuthorInfo(name, additions, deletions, data);
		theCopy.limitToDateRange(this.getDateRange());
		return theCopy;
	}

	@Override
	public String toString() {

		StringBuilder value = new StringBuilder();

		value.append("Name: " + name + ", ");
		value.append("Commits: " + getCommitCount() + ", ");
		value.append("Additions: " + getAdditions() + ", ");
		value.append("Deletions: ");
		value.append(getDeletions());
		value.append("\n");

		for (AuthorCommit ac : getCommits()) {
			value.append(" - " + ac.toString() + "\n\n");
		}

		value.append("\n");

		return value.toString();
	}

}
