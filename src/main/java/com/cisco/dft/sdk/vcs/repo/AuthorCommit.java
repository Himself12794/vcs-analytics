package com.cisco.dft.sdk.vcs.repo;

import java.util.Date;

import com.cisco.dft.sdk.vcs.util.DateLimitedData;
import com.cisco.dft.sdk.vcs.util.DateLimitedDataContainer.DateRange;

/**
 * Used to store information about a commit made by author.
 * 
 * @author phwhitin
 *
 */
public class AuthorCommit implements DateLimitedData {

	private final String id;

	private final long timestamp;

	private final int changedFiles;

	private final int additions;

	private final int deletions;

	private final int totalChange;

	private final boolean isMergeCommit;

	private final String message;

	public AuthorCommit(final String id, final long timestamp,
			final int changedFiles, final int additions, final int deletions,
			final boolean isMergeCommit, final String message) {

		this.id = id;
		this.timestamp = timestamp;
		this.changedFiles = changedFiles;
		this.additions = additions;
		this.deletions = deletions;
		this.totalChange = additions - deletions;
		this.isMergeCommit = isMergeCommit;
		this.message = message;

	}

	/**
	 * Gets the SHA-1 id.
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Unix-time. (in seconds)
	 * 
	 * @return
	 */
	public long getTimestamp() {
		return timestamp;
	}

	public int getChangedFiles() {
		return changedFiles;
	}

	public int getAdditions() {
		return additions;
	}

	public int getDeletions() {
		return deletions;
	}

	public int getTotalChange() {
		return totalChange;
	}

	public boolean isMergeCommit() {
		return isMergeCommit;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		String value = "Commit id: " + id;
		value += ", Timestamp: " + timestamp;
		value += ", Changed Files: " + changedFiles;
		value += ", Additions: " + additions;
		value += ", Deletions: " + deletions;
		value += ", Total Line Change: " + totalChange;
		value += isMergeCommit ? "\n\tMerge Commit, " : "\n\t";
		value += "Message: " + message;

		return value;
	}

	@Override
	public boolean isInDateRange(DateRange dateRange) {
		Date time = new Date(getTimestamp() * 1000);
		return dateRange.isInclusive() ? (time.getTime() >= dateRange.getStart().getTime() && time
				.getTime() <= dateRange.getEnd().getTime()) : (time.after(dateRange.getStart()) && time
				.before(dateRange.getEnd()));
	}

}
