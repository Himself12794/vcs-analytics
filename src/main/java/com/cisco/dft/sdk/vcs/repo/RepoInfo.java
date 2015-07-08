package com.cisco.dft.sdk.vcs.repo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.google.common.collect.Maps;

public class RepoInfo {
	
	private Map<String, BranchInfo> branches = Maps.newHashMap();
	
	/**
	 * Get the information for a specific branch.
	 * 
	 * @param branch for which to get information
	 * @return Information on the branch, or empty information if the branch does not exist.
	 * This is never null.
	 */
	public BranchInfo getBranchInfoFor(String branch) {
		
		String resolved = BranchInfo.branchNameResolver(branch);
		
		if (branches.containsKey(resolved)) { return branches.get(resolved); }
		else { return new BranchInfo(); }
		
	}
	
	BranchInfo getBranchInfo(String branch) {
		
		BranchInfo bi;
		
		if (!branches.containsKey(branch)) { 
			
			bi = new BranchInfo(branch);
			branches.put(branch, bi);
			
		} else { bi = branches.get(branch); }
		
		return bi;
		
	}
	
	/**
	 * Checks if the branch exists.
	 * 
	 * @param branch
	 * @return
	 */
	public boolean branchExists(String branch) {
		return branches.containsKey(branch);
	}
	
	/**
	 * Removes data for branches that no longer exist.
	 * 
	 * @param branches
	 */
	void resolveBranchInfo(List<String> branches) {
		
		for (String branch : this.branches.keySet()) {
			
			if (!branches.contains(branch)) {
				this.branches.remove(branch);
			}
			
		}
		
	}
	
	@Override
	public String toString() {
		
		Date date = new Date(System.currentTimeMillis()); 
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
		sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); 
		String formattedDate = sdf.format(date);
		
		StringBuilder value = new StringBuilder();
		
		value.append("++++++++++++++++++++++++++++++++++++\n");
		value.append("Time of report: ");
		value.append(formattedDate);
		value.append("\n");
		value.append("Branches in this report: ");
		
		for (String branch : this.branches.keySet()) {
			value.append(BranchInfo.branchTrimmer(branch));
			value.append(", ");
		}
		
		value.append("\n------------------------------------\n");
		
		for (BranchInfo bi : branches.values()) {
			value.append(bi.toString());
		}
		
		value.append("====================================\n");
		
		return value.toString();
		
	}

}
