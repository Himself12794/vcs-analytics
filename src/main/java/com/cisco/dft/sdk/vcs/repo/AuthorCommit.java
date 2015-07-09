package com.cisco.dft.sdk.vcs.repo;

/**
 * Used to store information about a commit made by author.
 * 
 * @author phwhitin
 *
 */
public class AuthorCommit {

	private final long timestamp;

	private final int changedFiles;

	private final int additions;

	private final int deletions;

	private final int totalChange;

	private final String message;

	public AuthorCommit(final long timestamp, final int changedFiles,
			final int additions, final int deletions, final int totalChange,
			final String message) {

		this.timestamp = timestamp;
		this.changedFiles = changedFiles;
		this.additions = additions;
		this.deletions = deletions;
		this.totalChange = totalChange;
		this.message = message;

	}

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
		value += ", message: " + message;

		return value;
	}

}
