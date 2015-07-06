package com.cisco.dft.sdk.vcs.repo;

import java.util.Map;

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
	
	@Override
	public String toString() {
		
		StringBuilder value = new StringBuilder();
		
		for (BranchInfo bi : branches.values()) {
			value.append(bi.toString());
		}
		
		return value.toString();
		
	}

}