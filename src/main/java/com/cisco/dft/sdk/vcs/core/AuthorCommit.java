package com.cisco.dft.sdk.vcs.core;

import java.util.Date;

import com.cisco.dft.sdk.vcs.common.DateLimitedData;
import com.google.common.collect.Range;

/**
 * Used to store information about a commit made by author.
 * 
 * @author phwhitin
 *
 */
public class AuthorCommit implements DateLimitedData {

	private final String id;

	private final Date timestamp;

	private final int changedFiles;

	private final int additions;

	private final int deletions;

	private final boolean isMergeCommit;

	private final String message;
	
	public AuthorCommit() {
		this("", new Date(0L), 0, 0, 0, false, "");
	}

	public AuthorCommit(final String id, final Date timestamp,
			final int changedFiles, final int additions, final int deletions,
			final boolean isMergeCommit, final String message) {

		this.id = id;
		this.timestamp = (Date) timestamp.clone();
		this.changedFiles = changedFiles;
		this.additions = additions;
		this.deletions = deletions;
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
	public Date getTimestamp() {
		return (Date) timestamp.clone();
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

	public boolean isMergeCommit() {
		return isMergeCommit;
	}

	public String getMessage() {
		return message;
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

	@Override
	public boolean isInDateRange(Range<Date> dateRange) {
		return dateRange.contains(timestamp);
	}

}
