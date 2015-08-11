package com.cisco.dft.sdk.vcs.core;

import java.util.Date;

import com.cisco.dft.sdk.vcs.util.DateLimitedData;
import com.google.common.collect.Range;

/**
 * Used to store information about a commit made by author.
 *
 * @author phwhitin
 *
 */
public class Commit implements DateLimitedData {

	private final String id;

	private final Date timestamp;

	private final int changedFiles;

	private final int additions;

	private final int deletions;

	private final boolean isMergeCommit;

	private final String message;

	public Commit() {
		this("", new Date(0L), 0, 0, 0, false, "");
	}

	public Commit(final String id, final Date timestamp, final int changedFiles,
			final int additions, final int deletions, final boolean isMergeCommit,
			final String message) {

		this.id = id;
		this.timestamp = (Date) timestamp.clone();
		this.changedFiles = changedFiles;
		this.additions = additions;
		this.deletions = deletions;
		this.isMergeCommit = isMergeCommit;
		this.message = message;

	}

	public int getAdditions() {
		return additions;
	}

	public int getChangedFiles() {
		return changedFiles;
	}

	public int getDeletions() {
		return deletions;
	}

	/**
	 * Gets the SHA-1 id.
	 *
	 * @return
	 */
	public String getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	/**
	 * Unix-time. (in seconds)
	 *
	 * @return
	 */
	public Date getTimestamp() {
		return (Date) timestamp.clone();
	}

	@Override
	public boolean isInDateRange(final Range<Date> dateRange) {
		return dateRange.contains(timestamp);
	}

	public boolean isMergeCommit() {
		return isMergeCommit;
	}

	@Override
	public String toString() {
		String value = "Id: " + id;
		value += ", Timestamp: " + timestamp;
		value += ", Changed Files: " + changedFiles;
		value += ", Additions: " + additions;
		value += ", Deletions: " + deletions;
		value += isMergeCommit ? "\n\tMerge Commit, " : "\n\t";
		value += "Message: " + message;

		return value;
	}

}