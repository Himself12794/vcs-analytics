package com.cisco.dft.sdk.vcs.repo;

/**
 * Used to store information about a commit made by author.
 * 
 * @author phwhitin
 *
 */
// TODO perhaps find a way to see total lines changed
public class AuthorCommit {

	private final long timestamp;

	private final int changedFiles;

	private final int additions;

	private final int deletions;

	private final String message;

	public AuthorCommit(final long timestamp, final int changedFiles,
			final int additions, final int deletions, final String message) {

		this.timestamp = timestamp;
		this.changedFiles = changedFiles;
		this.additions = additions;
		this.deletions = deletions;
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

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {

		String value = "Timestamp: " + timestamp;
		value += ", changed files: " + changedFiles;
		value += ", additions: " + additions;
		value += ", deletions: " + deletions;
		value += ", message: " + message;

		return value;
	}

}
