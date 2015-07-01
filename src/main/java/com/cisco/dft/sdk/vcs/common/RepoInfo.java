package com.cisco.dft.sdk.vcs.common;

import java.util.Map;
import java.util.Map.Entry;

import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;
import com.google.common.collect.Maps;

public class RepoInfo {

	private int fileCount;
	
	private int lineCount;
	
	private final Map<Language, Integer> languageCount;
	
	public RepoInfo() {
		
		this(0, 0, Maps.newHashMap());
		
	}
	
	public RepoInfo(int fileCount, int lineCount, final Map<Language, Integer> languageCount) {
		
		this.fileCount = fileCount;
		
		this.lineCount = lineCount;
		
		this.languageCount = languageCount;
		
	}
	
	public void incrementLanguage(Language lang, int x) {
		
		if (!languageCount.containsKey(lang)) {
			
			languageCount.put(lang, x);
			
		} else {
			
			languageCount.put(lang, languageCount.get(lang) + x);
			
		}
		
	} 
	
	public int getFileCount() { return fileCount; }
	
	public void incrementFileCount(int x) {	fileCount += x; }
	
	public int getLineCount() { return lineCount; }

	public void incrementLineCount(int x) { lineCount += x; }
	
	public float getLangPercent(Language lang) {
		return languageCount.containsKey(lang) ? languageCount.get(lang).floatValue() / fileCount : 0.0F;	
	}
	
	public float getLangCount(Language lang) {
		return languageCount.containsKey(lang) ? languageCount.get(lang) : 0;	
	}
	
	public Map<Language, Integer> getLangCountMap() {
		return Maps.newHashMap(languageCount);		
	}
	
	@Override
	public String toString() {
		
		String value = "File Count: " + fileCount;
		value += "\nLine Count: " + lineCount;
		value += "\nLanguage Stats:\n\n";
		
		for (Entry<Language, Integer> entry : this.languageCount.entrySet()) {
			
			value += " " + entry.getKey().name() + ": \n\tcount: " + entry.getValue() + "\n\tpercentage: " + String.format("%.1f", (entry.getValue().floatValue() * 100)/ fileCount) + "%";
			value += "\n\n";
		}
		
		return value;
	}
	
	@Override
	public RepoInfo clone() {
		return new RepoInfo(fileCount, lineCount, Maps.newHashMap(languageCount));
	}
	
}
