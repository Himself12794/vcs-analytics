package com.cisco.dft.sdk.vcs.repo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class BranchInfo {

	private int fileCount;
	
	private int lineCount;
	
	private final String branch;
	
	private final Map<Language, Integer> languageCount;
	
	private final Map<String, AuthorInfo> authorInfo;
	
	public BranchInfo() {
		this("Unknown");
	}
	
	public BranchInfo(final String branch) {
		this(0, 0, branch);
	}
	
	public BranchInfo(int fileCount, int lineCount, final String branch) {
		this(fileCount, lineCount, branch, new HashMap<Language, Integer>(), new HashMap<String, AuthorInfo>());
	}
	
	private BranchInfo(int fileCount, int lineCount, final String branch, final Map<Language, Integer> languageCount, final Map<String, AuthorInfo> authorInfo) {
		
		this.fileCount = fileCount;
		this.lineCount = lineCount;
		this.branch = branch;
		this.languageCount = languageCount;
		this.authorInfo = authorInfo;
		
	}
	
	void incrementLanguage(Language lang, int x) {
		
		if (!languageCount.containsKey(lang)) {
			
			languageCount.put(lang, x);
			
		} else {
			
			languageCount.put(lang, languageCount.get(lang) + x);
			
		}
		
	}
	
	void resetInfo() {
		
		for (Language lang : languageCount.keySet()) {
			languageCount.put(lang, 0);
		}
		
		setFileCount(0);
		setLineCount(0);
		
	}
	
	/**
	 * Get the percentage of the repo that is made up of this language.
	 * 
	 * @param lang
	 * @return
	 */
	public float getLangPercent(Language lang) {
		return languageCount.containsKey(lang) ? languageCount.get(lang).floatValue() / fileCount : 0.0F;	
	}
	
	/**
	 * Get number of files that us the specified language.
	 * 
	 * @param lang
	 * @return
	 */
	public int getLangCount(Language lang) {
		return languageCount.containsKey(lang) ? languageCount.get(lang) : 0;	
	}
	
	public int getFileCount() { return fileCount; }
	
	private void setFileCount(int count) { fileCount = count; }
	
	void incrementFileCount(int x) { fileCount += x; }
	
	public int getLineCount() { return lineCount; }
	
	private void setLineCount(int count) { lineCount = count; }

	void incrementLineCount(int x) { lineCount += x; }
	
	public String getBranch() {
		return branch;
	}
	
	public Map<Language, Integer> getLangCountMap() {
		return Maps.newHashMap(languageCount);		
	}
	
	/**
	 * Gets the statistics that have been logged for the repo.
	 * The data is stored in memory for efficiency, so data may be inaccurate
	 * unless {@link GitRepo#sync()} is run.
	 * <p>
	 * Because of the potential size of some repositories, this is not updated
	 * unless the user initializes the repository with autoSync true, or manually
	 * runs {@link GitRepo#sync()}
	 * 
	 * @param sync whether or not the local should sync with remote
	 * @return a statistics builder for this repo 
	 */
	public AuthorInfoBuilder getAuthorStatistics() {
		
		return new AuthorInfoBuilder(Lists.newArrayList(authorInfo.values()), branch);
		
	}
	
	AuthorInfo getAuthorInfo(String author) {
		
		AuthorInfo ai;
		
		if (!authorInfo.containsKey(author)) { 
			
			ai = new AuthorInfo(author);
			authorInfo.put(author, ai);
			
		} else { ai = authorInfo.get(author); }
		
		return ai;
		
	}
	
	@Override
	public String toString() {
		
		StringBuilder value = new StringBuilder("Branch: ");
		value.append(branch);
		value.append("\nFile Count: ");
		value.append(fileCount);
		value.append("\nLine Count: ");
		value.append(lineCount);
		value.append("\nLanguage Stats:\n\n");
		
		for (Entry<Language, Integer> entry : this.languageCount.entrySet()) {
			
			value.append(" " + entry.getKey().name() + ": \n\tcount: " + entry.getValue() + "\n\tpercentage: " + String.format("%.1f", (entry.getValue().floatValue() * 100)/ fileCount) + "%");
			value.append("\n\n");
		}
		
		return value.toString();
	}
	
	/**
	 * Clips the "refs/heads/" prefix off branch names.
	 * 
	 * @param branch
	 * @return the freshly trimmed branch
	 */
	public static String branchTrimmer(String branch) {
		return branch.replace("refs/heads/", "");
	}
	
}
