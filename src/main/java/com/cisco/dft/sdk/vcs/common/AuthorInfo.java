package com.cisco.dft.sdk.vcs.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Wrapper class used for author data pulled from the repository.
 * 
 * @author phwhitin
 *
 */
public class AuthorInfo {
	
	/**Used when no author of a given name is found*/
	public static final AuthorInfo NOTFOUND = new AuthorInfo("John Doe");
	
	private final String name;
	
	private int additions;
	
	private int deletions;
	
	private List<AuthorCommit> commits = new ArrayList<AuthorCommit>();
	
	public AuthorInfo(final String name) {
		this(name, 0, 0);
	}
	
	public AuthorInfo(final String name, int additions, int deletions) {
		
		this.name = name;
		this.additions = additions;
		this.deletions = deletions;
		
	}
	
	/**
	 * Adds commit and makes sure it is not a duplicate.
	 * 
	 * @param ac
	 */
	public void addCommit(AuthorCommit ac) {
		
		for (AuthorCommit c : commits) {
		
			if (c.getTimestamp() == ac.getTimestamp()) { return; }
		
		}
		
		this.commits.add(ac);
	}
	
	public List<AuthorCommit> getCommits() {
		
		Comparator<AuthorCommit> sorter = new Comparator<AuthorCommit>() {

			@Override
			public int compare(AuthorCommit p1, AuthorCommit p2) {
				
				return Long.compare(p2.getTimestamp(), p1.getTimestamp());
			}
			
		};
		
		Collections.sort(this.commits,sorter);
		
		return this.commits;
	}
	
	public String getName() {return this.name;}
	
	public int getCommitCount() {return this.commits.size();}
	
	public int getAdditions() {return this.additions;}
	
	public void incrementAdditions(int x) {this.additions += x;}
	
	public int getDeletions() {return this.deletions;}
	
	public void incrementDeletions(int x) {this.deletions += x;}
	
	public int getAuthorCount() {
		return getCommits().size();		
	}
	
	public AuthorInfo copy() {
		
		AuthorInfo theClone = new AuthorInfo(name, additions, deletions);
		theClone.commits = Lists.newArrayList(this.commits);
		return theClone;
		
	}
	
	@Override
	public String toString() {
		
		StringBuilder value = new StringBuilder();
				
		value.append("Name: " + name + ", ");
		value.append("Commits: " + getCommitCount() + ", ");
		value.append("Additions: " + additions + ", ");
		value.append("Deletions: " + deletions + "\n");
		
		for (AuthorCommit ac : this.getCommits()) {
			
			value.append(" - " + ac.toString() + "\n");
			
		}
		
		value.append("\n");
		
		return value.toString();
	}
	
}
