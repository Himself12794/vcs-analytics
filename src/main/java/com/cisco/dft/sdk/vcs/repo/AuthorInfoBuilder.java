package com.cisco.dft.sdk.vcs.repo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.cisco.dft.sdk.vcs.util.SortMethod;

/**
 * Utility object for organizing printed author output and looking
 * up specific users.
 * 
 * @author phwhitin
 *
 */
public class AuthorInfoBuilder {
	
	private List<AuthorInfo> infos;
	
	private String branch;
	
	AuthorInfoBuilder(List<AuthorInfo> infos) {
		this(infos, "Could not detect");
	}
	
	AuthorInfoBuilder(List<AuthorInfo> infos, String branch) {
		this.infos = infos;
		this.branch = branch;
	}
	
	/**
	 * Sorts the output by the specified method.
	 * 
	 * @param method the sort method to use
	 * @return the builder instance
	 */
	public AuthorInfoBuilder sort(SortMethod method){
		
		switch (method) {
			case COMMITS:
				Collections.sort(this.infos, new Comparator<AuthorInfo>() {
					@Override public int compare(AuthorInfo p1, AuthorInfo p2) { return Long.compare(p2.getCommitCount(), p1.getCommitCount()); } 
				});
				break;
			case ADDITIONS: 
				Collections.sort(this.infos, new Comparator<AuthorInfo>() {
					@Override public int compare(AuthorInfo p1, AuthorInfo p2) { return Long.compare(p2.getAdditions(), p1.getAdditions());	}
				});
				break;
			case DELETIONS:
				Collections.sort(this.infos, new Comparator<AuthorInfo>() {
					@Override public int compare(AuthorInfo p1, AuthorInfo p2) { return Long.compare(p2.getDeletions(), p1.getDeletions()); }
				});
				break;
			case NAME: 
				Collections.sort(this.infos, new Comparator<AuthorInfo>() {
					@Override public int compare(AuthorInfo p1, AuthorInfo p2) { return p1.getName().compareTo(p2.getName()); }
				});
				break;
			default:
				break;
		}
		
		return this;
		
	}
	
	/**
	 * Looks up information for a specific user. Don't forget to sync to make sure
	 * that the user information exists.
	 * <p>
	 * <b><u>Note</u></b>: it is possible that the same person have committed
	 * to the repository using different names, so this is not a catch-all for 
	 * a specific person. Make sure you know what name the persons uses to commit
	 * with before looking for them.
	 * 
	 * @param user
	 * @return a copy of the AuthorInfo for this user if it exists, or an empty AuthorInfo object.
	 */
	public AuthorInfo lookupUser(String user) {
		
		for (AuthorInfo ai : infos) {
			if (ai.getName().equals(user)) { return ai; }
		}
		
		return new AuthorInfo(user);
	}
	
	/**
	 * Gets the statistics for the repo.
	 * 
	 * @return a list of AuthorInfo stored for the repo
	 */
	public List<AuthorInfo> getList() { return this.infos; }
	
	/**
	 * Returns the branch that this information is from.
	 * 
	 * @return
	 */
	public String getBranchName() {
		
		return branch;
		
	}
	
	@Override
	public String toString() {
		
		StringBuilder value = new StringBuilder("Branch: ");
		value.append(getBranchName());
		value.append("\n");
		
		for (AuthorInfo ai : infos) { value.append(ai.toString()); }
		
		value.append("\n");
		
		return value.toString();
		
	}
	
}