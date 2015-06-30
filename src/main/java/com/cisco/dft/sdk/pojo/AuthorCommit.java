package com.cisco.dft.sdk.pojo;

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
	
}


