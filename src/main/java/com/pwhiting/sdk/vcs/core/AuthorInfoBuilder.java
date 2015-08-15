package com.pwhiting.sdk.vcs.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.pwhiting.sdk.vcs.core.error.CommitterNotFoundException;
import com.pwhiting.sdk.vcs.core.util.SortMethod;
import com.pwhiting.sdk.vcs.util.DateLimitedDataContainer;
import com.pwhiting.sdk.vcs.util.Util;

/**
 * Utility object for organizing printed author output and looking up specific
 * users. All information is copied, so worries in compromising internal data.
 * The downside is that a new instance must be acquired after every sync.
 *
 * @author phwhitin
 *
 */
public class AuthorInfoBuilder {

	private static final Comparator<CommitterInfo> SORTER_COMMITS = new Comparator<CommitterInfo>() {
		@Override
		public int compare(final CommitterInfo p1, final CommitterInfo p2) {
			return Long.compare(p2.getCommitCount(), p1.getCommitCount());
		}
	};

	private static final Comparator<CommitterInfo> SORTER_ADDITIONS = new Comparator<CommitterInfo>() {
		@Override
		public int compare(final CommitterInfo p1, final CommitterInfo p2) {
			return Long.compare(p2.getAdditions(), p1.getAdditions());
		}
	};

	private static final Comparator<CommitterInfo> SORTER_DELETIONS = new Comparator<CommitterInfo>() {
		@Override
		public int compare(final CommitterInfo p1, final CommitterInfo p2) {
			return Long.compare(p2.getDeletions(), p1.getDeletions());
		}
	};

	private static final Comparator<CommitterInfo> SORTER_NAMES = new Comparator<CommitterInfo>() {
		@Override
		public int compare(final CommitterInfo p1, final CommitterInfo p2) {
			return p1.getCommitterName().compareTo(p2.getCommitterName());
		}
	};

	private final String branch;

	private final DateLimitedDataContainer<CommitterInfo> authorInfo;

	AuthorInfoBuilder(final List<CommitterInfo> infos) {
		this(infos, "Could not detect");
	}

	AuthorInfoBuilder(final List<CommitterInfo> infos, final String branch) {

		final List<CommitterInfo> copiedList = Lists.newArrayList();
		for (final CommitterInfo ai : infos) {
			copiedList.add(ai.copy());
		}

		authorInfo = new DateLimitedDataContainer<CommitterInfo>(copiedList);
		this.branch = branch;
	}

	/**
	 * Returns the branch that this information is from.
	 *
	 * @return
	 */
	public String getBranchName() {
		return BranchInfo.branchTrimmer(branch);
	}

	/**
	 * Gets the statistics for the repo.
	 *
	 * @return a list of AuthorInfo stored for the repo
	 */
	public List<CommitterInfo> getInfo() {
		return Lists.newArrayList(authorInfo.getData());
	}

	public int getTotalCommitCount() {
		int count = 0;

		for (final CommitterInfo ai : authorInfo.getData()) {
			count += ai.getCommitCount();
		}

		return count;
	}

	/**
	 * Remove the date limitations placed.
	 *
	 * @return
	 */
	public AuthorInfoBuilder includeAll() {

		authorInfo.includeAll();

		return this;
	}

	/**
	 * Limit author information returned to those who have committed within
	 * given time frame.
	 * <p>
	 * See {@link Range} for information on how to use ranges.
	 *
	 * @param dateRange
	 *            the range to use
	 * 
	 * @return
	 */
	public AuthorInfoBuilder limitToDateRange(final Range<Date> dateRange) {
		authorInfo.limitToDateRange(dateRange);
		return this;
	}

	/**
	 * Limit author information returned to those who have committed within
	 * given time frame.
	 *
	 * @param start
	 *            the start date
	 * @param end
	 *            the end date
	 * 
	 * @return
	 */
	public AuthorInfoBuilder limitToRange(final Date start, final Date end) {
		return limitToDateRange(Util.getAppropriateRange(start, end));
	}

	/**
	 * Looks up information for a specific user. Don't forget to sync to make
	 * sure that the user information exists.
	 * <p>
	 * <b><u>Note</u></b>: it is possible that the same person have committed to
	 * the repository using different names, so this is not a catch-all for a
	 * specific person. Make sure you know what name the persons uses to commit
	 * with before looking for them.
	 *
	 * @param user
	 * @return a copy of the AuthorInfo for this user if it exists, or an empty
	 *         AuthorInfo object.
	 * @throws CommitterNotFoundException 
	 */
	public CommitterInfo lookupCommitter(final String user) throws CommitterNotFoundException {
		
		for (final CommitterInfo ai : authorInfo.getData()) {
			if (ai.getCommitterName().equals(user)) { 
				return ai;
			}
		}
		
		throw new CommitterNotFoundException("Committer " + user + " not found.");
	}
	
	/**
	 * Looks up the user by email. If more than one user exists with the same email, it gives the first one.
	 * 
	 * @param email
	 * @return
	 * @throws CommitterNotFoundException
	 */
	public CommitterInfo lookupUserByEmail(final String email) throws CommitterNotFoundException {
		
		for (final CommitterInfo ci : authorInfo.getData()) {
			if (ci.getCommitterEmail().equals(email)) { return ci; }
		}
		
		throw new CommitterNotFoundException("Committer with email " + email + " not found.");
		
	}

	/**
	 * Sorts the output by the specified method.
	 *
	 * @param method
	 *            the sort method to use
	 * @return the builder instance
	 */
	public AuthorInfoBuilder sort(final SortMethod method) {

		switch (method) {
			case COMMITS:
				Collections.sort(authorInfo.getData(), SORTER_COMMITS);
				break;
			case ADDITIONS:
				Collections.sort(authorInfo.getData(), SORTER_ADDITIONS);
				break;
			case DELETIONS:
				Collections.sort(authorInfo.getData(), SORTER_DELETIONS);
				break;
			case NAME:
				Collections.sort(authorInfo.getData(), SORTER_NAMES);
				break;
			default:
				break;
		}

		return this;

	}

	@Override
	public String toString() {

		final StringBuilder value = new StringBuilder("Branch: ");
		value.append(getBranchName());
		value.append("\n");
		value.append("Date range: ");
		value.append(authorInfo.getDateRange().hasLowerBound() ? authorInfo.getDateRange()
				.lowerEndpoint() : "first-commit");
		value.append(" - ");
		value.append(authorInfo.getDateRange().hasUpperBound() ? authorInfo.getDateRange()
				.upperEndpoint() : "most-recent-commit");
		value.append("\n");
		value.append("Total Commit Count: ");
		value.append(getTotalCommitCount());
		value.append("\n\n");

		for (final CommitterInfo ai : authorInfo.getData()) {
			value.append(ai.toString());
		}

		return value.toString();

	}

}