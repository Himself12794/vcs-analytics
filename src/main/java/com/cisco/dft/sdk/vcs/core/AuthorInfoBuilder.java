package com.cisco.dft.sdk.vcs.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.cisco.dft.sdk.vcs.common.DateLimitedDataContainer;
import com.cisco.dft.sdk.vcs.common.SortMethod;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

/**
 * Utility object for organizing printed author output and looking
 * up specific users. All information is copied, so worries in compromising
 * internal data. The downside is that a new instance must be acquired after
 * every sync.
 * 
 * @author phwhitin
 *
 */
public class AuthorInfoBuilder {
	
	private String branch;
	
	private DateLimitedDataContainer<AuthorInfo> authorInfo;
	
	AuthorInfoBuilder(final List<AuthorInfo> infos) {
		this(infos, "Could not detect");
	}
	
	AuthorInfoBuilder(final List<AuthorInfo> infos, String branch) {
		
		List<AuthorInfo> copiedList = Lists.newArrayList();
		for (AuthorInfo ai : infos) {
			copiedList.add(ai.copy());
		}
		
		authorInfo = new DateLimitedDataContainer<AuthorInfo>(copiedList);
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
				Collections.sort(authorInfo.getData(), SORTER_COMMITS); 
				break;
			case ADDITIONS: 
				Collections.sort(authorInfo.getData(), SORTER_ADDITIONS );
				break;
			case DELETIONS:
				Collections.sort(authorInfo.getData(), SORTER_DELETIONS);
				break;
			case NAME: 
				Collections.sort(authorInfo.getData(), SORTER_NAMES);
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
		
		for (AuthorInfo ai : authorInfo.getData()) {
			if (ai.getName().equals(user)) { return ai; }
		}
		
		return new AuthorInfo(user);
	}
	
	/**
	 * Gets the statistics for the repo.
	 * 
	 * @return a list of AuthorInfo stored for the repo
	 */
	public List<AuthorInfo> getInfo() { return Lists.newArrayList(authorInfo.getData()); }
	
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
	 * @return
	 */
	public AuthorInfoBuilder limitToDateRange(Range<Date> dateRange) {
		authorInfo.limitToDateRange(dateRange);
		return this;
	}
	
	/**
	 * Remove the date limitations placed.
	 * 
	 * @return
	 */
	public AuthorInfoBuilder includeAll() {
		
		authorInfo.includeAll();
		
		return this;
	}
	
	@Override
	public String toString() {
		
		StringBuilder value = new StringBuilder("Branch: ");
		value.append(getBranchName());
		value.append("\n");
		
		for (AuthorInfo ai : authorInfo.getData()) { value.append(ai.toString()); }
		
		return value.toString();
		
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