package com.cisco.dft.sdk.vcs.repo;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.cisco.dft.sdk.vcs.util.DateLimitedDataContainerRecursive;
import com.cisco.dft.sdk.vcs.util.SortMethod;
import com.google.common.collect.Lists;

/**
 * Utility object for organizing printed author output and looking
 * up specific users.
 * 
 * @author phwhitin
 *
 */
public class AuthorInfoBuilder extends DateLimitedDataContainerRecursive<AuthorInfo> {
	
	private String branch;
	
	AuthorInfoBuilder(final List<AuthorInfo> infos) {
		this(infos, "Could not detect");
	}
	
	AuthorInfoBuilder(final List<AuthorInfo> infos, String branch) {
		super(infos);
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
				Collections.sort(data, SORTER_COMMITS); 
				Collections.sort(limitedData, SORTER_COMMITS); 
				break;
			case ADDITIONS: 
				Collections.sort(data, SORTER_ADDITIONS );
				Collections.sort(limitedData, SORTER_ADDITIONS );
				break;
			case DELETIONS:
				Collections.sort(data, SORTER_DELETIONS);
				Collections.sort(limitedData, SORTER_DELETIONS);
				break;
			case NAME: 
				Collections.sort(data, SORTER_NAMES);
				Collections.sort(limitedData, SORTER_NAMES);
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
		
		if (!isLimited()) {
			
			for (AuthorInfo ai : data) {
				if (ai.getName().equals(user)) { return ai; }
			}
			
		} else {
			
			for (AuthorInfo ai : limitedData) {
				if (ai.getName().equals(user)) { return ai; }
			}
			
		}
		
		return new AuthorInfo(user);
	}
	
	/**
	 * Gets the statistics for the repo.
	 * 
	 * @return a list of AuthorInfo stored for the repo
	 */
	@Override
	public List<AuthorInfo> getData() { return Lists.newArrayList(super.getData()); }
	
	/**
	 * Returns the branch that this information is from.
	 * 
	 * @return
	 */
	public String getBranchName() { return BranchInfo.branchTrimmer(branch); }
	
	/**
	 * Limit author information returned to those who have committed within given time frame.
	 * 
	 * @param start the start date
	 * @param end the end date
	 * @param inclusive whether or not the range includes the endpoints
	 * @return
	 */
	@Override
	public void limitToDateRange(Date start, Date end, boolean inclusive) {
		super.limitToDateRange(start, end, inclusive);
	}
	
	/**
	 * Remove the date limitations placed.
	 * 
	 * @return
	 */
	@Override
	public AuthorInfoBuilder includeAll() {
		
		super.includeAll();
		
		return this;
	}
	
	@Override
	public String toString() {
		
		StringBuilder value = new StringBuilder("Branch: ");
		value.append(getBranchName());
		value.append("\n");
		
		for (AuthorInfo ai : getData()) { value.append(ai.toString()); }
		
		return value.toString();
		
	}

	@Override
	public boolean isInDateRange(Date start, Date end, boolean inclusive) {
		
		boolean flag = false;
		for (AuthorInfo ai : data) {
			flag |= ai.isInDateRange(start, end, inclusive);
		}
		return flag;
	}
	
	private static final Comparator<AuthorInfo> SORTER_COMMITS = new Comparator<AuthorInfo>() {
		@Override 
		public int compare(AuthorInfo p1, AuthorInfo p2) { return Long.compare(p2.getCommitCount(), p1.getCommitCount()); } 
	};
	
	private static final Comparator<AuthorInfo> SORTER_ADDITIONS = new Comparator<AuthorInfo>() {
		@Override 
		public int compare(AuthorInfo p1, AuthorInfo p2) { return Long.compare(p2.getAdditions(), p1.getAdditions());	}
	};
	
	private static final Comparator<AuthorInfo> SORTER_DELETIONS = new Comparator<AuthorInfo>() {
		@Override 
		public int compare(AuthorInfo p1, AuthorInfo p2) { return Long.compare(p2.getDeletions(), p1.getDeletions()); }
	};
	
	private static final Comparator<AuthorInfo> SORTER_NAMES = new Comparator<AuthorInfo>() {
		@Override 
		public int compare(AuthorInfo p1, AuthorInfo p2) { return p1.getName().compareTo(p2.getName()); }
	};
	
}