package com.cisco.dft.sdk.pojo;

import java.util.HashSet;
import java.util.Set;

/**
 * Wrapper class used for author data pulled from the repository.
 * 
 * @author phwhitin
 *
 */
public class AuthorInfo {
	
	private final String name;
	
	private int commits;
	
	private int additions;
	
	private int deletions;
	
	private Set<AuthorCommit> commitList = new HashSet<AuthorCommit>();
	
	public AuthorInfo(final String name, int commits, int additions, int deletions) {
		
		this.name = name;
		this.commits = commits;
		this.additions = additions;
		this.deletions = deletions;
		
	}
	
	public void addCommit(AuthorCommit ac) {this.commitList.add(ac);}
	
	public Set<AuthorCommit> getCommitList() {return this.commitList;}
	
	public String getName() {return this.name;}
	
	public int getCommits() {return this.commits;}
	
	public void incrementCommits() {++this.commits;}
	
	public int getAdditions() {return this.additions;}
	
	public void incrementAdditions(int x) {this.additions += x;}
	
	public int getDeletions() {return this.deletions;}
	
	public void incrementDeletions(int x) {this.deletions += x;}
	
	@Override
	public String toString() {
		String value = "";
		value += "Name: " + name + ", ";
		value += "Commits: " + commits + ", ";
		value += "Additions: " + additions + ", ";
		value += "Deletions: " + deletions;
		
		return value;
	}
	
}
