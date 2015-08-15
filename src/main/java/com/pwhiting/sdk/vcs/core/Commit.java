package com.pwhiting.sdk.vcs.core;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Range;
import com.pwhiting.util.DateLimitedData;

/**
 * Used to store information about a commit made by author.
 *
 * @author phwhitin
 *
 */
public class Commit implements DateLimitedData {

	private final String id;

	private final long timestamp;

	private final int changedFiles;

	private final int additions;

	private final int deletions;
	
	@JsonProperty("mergeCommit")
	private final boolean isMergeCommit;

	private final String message;
	
	private String committer;

	public Commit() {
		this("", new Date(0L), 0, 0, 0, false, "");
	}

	public Commit(final String id, final Date timestamp, final int changedFiles,
			final int additions, final int deletions, final boolean isMergeCommit,
			final String message) {

		this.id = id;
		this.timestamp = timestamp.getTime();
		this.changedFiles = changedFiles;
		this.additions = additions;
		this.deletions = deletions;
		this.isMergeCommit = isMergeCommit;
		this.message = message;

	}
	
	void setCommitter(final String name) {
		committer = name;
	}
	
	public String getCommitter() {
		return committer;
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

	public Date getTimestamp() {
		return new Date(timestamp);
	}

	@Override
	public boolean isInDateRange(final Range<Date> dateRange) {
		return dateRange.contains(getTimestamp());
	}

	public boolean isMergeCommit() {
		return isMergeCommit;
	}
	
	public boolean isTheSame(Commit other) {
		return this.id.equals(other.id) && this.timestamp == other.timestamp;
	}

	@Override
	public String toString() {
		String value = "Id: " + id;
		value += ", Timestamp: " + getTimestamp();
		value += ", Changed Files: " + changedFiles;
		value += ", Additions: " + additions;
		value += ", Deletions: " + deletions;
		value += isMergeCommit ? "\n\tMerge Commit, " : "\n\t";
		value += "Message: " + message;

		return value;
	}

}
