package com.cisco.dft.sdk.vcs.repo;

/**
 * Used to store information about a commit made by author.
 * 
 * @author phwhitin
 *
 */
// TODO include commit message in this class
public class AuthorCommit {
	
	private final long timestamp;
	
	private final int additions;
	
	private final int deletions;
	
	public AuthorCommit(long timestamp, int additions, int deletions) {
		
		this.timestamp = timestamp;
		this.additions = additions;
		this.deletions = deletions;
		
	}

	public long getTimestamp() {
		return timestamp;
	}

	public int getAdditions() {
		return additions;
	}

	public int getDeletions() {
		return deletions;
	}
	
	@Override
	public String toString() {
		
		String value = "Timestamp: " + timestamp;
		value += ", additions: " + additions;
		value += ", deletions: " + deletions;
		
		return value;
	}
	
}


