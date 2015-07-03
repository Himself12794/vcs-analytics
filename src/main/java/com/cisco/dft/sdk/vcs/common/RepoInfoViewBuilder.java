package com.cisco.dft.sdk.vcs.common;

import java.util.List;

public class RepoInfoViewBuilder {
	
	private List<BranchInfo> infos;
	
	public RepoInfoViewBuilder(List<BranchInfo> infos) {
		this.infos = infos;
	}
	
	/**
	 * Sorts the output by the specified method.
	 * 
	 * @param method the sort method to use
	 * @return the builder instance
	 */
	/*public AuthorInfoViewBuilder sort(SortMethod method){
		
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
		
	}*/
	
	/**
	 * Looks up information for a specific user. Don't forget to sync to make sure
	 * that the user information exists.
	 * <p>
	 * <b><u>Note</u></b>: it is possible that the same person have committed
	 * to the repository using different names, so this is not a catch-all.
	 * 
	 * @param user
	 * @return a copy of the AuthorInfo for this user if it exists, or an empty AuthorInfo object.
	 */
	public BranchInfo lookupBranch(String branch) {
		
		for (BranchInfo ri : infos) {
			
			if (ri.getBranch().equals(branch)) { return ri.copy(); }
			
		}
		
		return new BranchInfo(branch);
	}
	
	/**
	 * Gets the statistics for the repo.
	 * 
	 * @return a list of AuthorInfo stored for the repo
	 */
	public List<BranchInfo> getList() { return this.infos; }
	
	@Override
	public String toString() {
		
		StringBuilder value = new StringBuilder();
			
		for (BranchInfo ai : infos) { value.append(ai.toString()); }
		
		value.append("\n");
			
		
		return value.toString();
	}
	
}
