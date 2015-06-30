package com.cisco.dft.sdk.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class used for author data pulled from the repository.
 * 
 * @author phwhitin
 *
 */
public class AuthorInfo {
	
	private final String name;
	
	private int commitCount;
	
	private int additions;
	
	private int deletions;
	
	private List<AuthorCommit> commits = new ArrayList<AuthorCommit>();
	
	public AuthorInfo(final String name) {
		this(name, 0, 0, 0);
	}
	
	public AuthorInfo(final String name, int commits, int additions, int deletions) {
		
		this.name = name;
		this.commitCount = commits;
		this.additions = additions;
		this.deletions = deletions;
		
	}
	
	/**
	 * Adds commit and makes sure it is not a duplicate.
	 * 
	 * @param ac
	 */
	public void addCommit(AuthorCommit ac) {
		
		for (AuthorCommit c : commits)
			if (c.getTimestamp() == ac.getTimestamp()) return;
		
		this.commits.add(ac);
	}
	
	public List<AuthorCommit> getCommits() {
		
		this.commits.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp()));
		
		return this.commits;
	}
	
	public String getName() {return this.name;}
	
	public int getCommitCount() {return this.commitCount;}
	
	public void incrementCommitCount() {++this.commitCount;}
	
	public int getAdditions() {return this.additions;}
	
	public void incrementAdditions(int x) {this.additions += x;}
	
	public int getDeletions() {return this.deletions;}
	
	public void incrementDeletions(int x) {this.deletions += x;}
	
	@Override
	public String toString() {
		String value = "";
		value += "Name: " + name + ", ";
		value += "Commits: " + commitCount + ", ";
		value += "Additions: " + additions + ", ";
		value += "Deletions: " + deletions;
		
		return value;
	}
	
}
