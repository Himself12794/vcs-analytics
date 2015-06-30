package com.cisco.dft.sdk.pojo;

/**
 * Wrapper class used to store information about a commit.
 * 
 * @author phwhitin
 *
 */
public class AuthorCommit {
	
	private final long timestamp;
	
	private final int additions;
	
	private final int deletions;
	
	public AuthorCommit(final long timestamp, final int additions, final int deletions) {
		
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
	
}


