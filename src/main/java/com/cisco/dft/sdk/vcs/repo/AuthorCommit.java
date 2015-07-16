package com.cisco.dft.sdk.vcs.repo;

import java.util.Date;

import com.cisco.dft.sdk.vcs.util.DateLimitedData;

/**
 * Used to store information about a commit made by author.
 * 
 * @author phwhitin
 *
 */
public class AuthorCommit implements DateLimitedData {

	private final long timestamp;

	private final int changedFiles;

	private final int additions;

	private final int deletions;

	private final int totalChange;

	private final boolean isMergeCommit;

	private final String message;

	public AuthorCommit(final long timestamp, final int changedFiles,
			final int additions, final int deletions, final int totalChange,
			final boolean isMergeCommit, final String message) {

		this.timestamp = timestamp;
		this.changedFiles = changedFiles;
		this.additions = additions;
		this.deletions = deletions;
		this.totalChange = totalChange;
		this.isMergeCommit = isMergeCommit;
		this.message = message;

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

		String value = "Timestamp: " + timestamp;
		value += ", changed files: " + changedFiles;
		value += ", additions: " + additions;
		value += ", deletions: " + deletions;
		value += ", total line change: " + totalChange;
		value += ", merge commit: " + isMergeCommit;
		value += ", message: " + message;

		return value;
	}

	@Override
	public boolean isInDateRange(Date start, Date end, boolean inclusive) {
		Date time = new Date(getTimestamp() * 1000);
		return  inclusive ? (time.getTime() >= start.getTime() && time
				.getTime() <= end.getTime()) : (time.after(start) && time
				.before(end));
	}

}
