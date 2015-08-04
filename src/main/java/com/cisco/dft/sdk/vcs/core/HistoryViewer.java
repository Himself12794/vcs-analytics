package com.cisco.dft.sdk.vcs.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cisco.dft.sdk.vcs.common.util.CodeSniffer.Language;
import com.cisco.dft.sdk.vcs.common.util.Util;
import com.cisco.dft.sdk.vcs.core.ClocData.LangStats;

public class HistoryViewer {

	protected final Repo theRepo;

	private final String history;

	boolean usesCLOCStats = false;

	protected Date theDate;

	protected final String branch;

	protected final ClocData data;

	HistoryViewer(Repo theRepo, String ac, Date date) {
		this("Unknown", theRepo, ac, date);
	}

	protected HistoryViewer(final String branch, Repo theRepo, String ac,
			Date date) {
		this(branch, theRepo, ac, date, new HashMap<Language, Integer>(), new ClocData());
	}

	protected HistoryViewer(final String branch, Repo theRepo, String ac,
			Date date, final Map<Language, Integer> languageCount, ClocData data) {

		this.branch = branch;
		this.history = ac;
		this.theDate = date;
		this.theRepo = theRepo;
		this.data = data;

	}

	protected void setDate(Date date) {
		theDate = date;
	}

	public int getFileCount() {
		return data.getHeader().getnFiles();
	}

	protected void setFileCount(int fileCount) {
		data.getHeader().setnFiles(fileCount);
	}

	public int getLineCount() {
		return data.getHeader().getnLines();
	}

	protected void setLineCount(int lineCount) {
		data.getHeader().setnLines(lineCount);
	}

	void incrementFileCount(int x) {
		setFileCount(getFileCount() + x);
	}

	void incrementLineCount(int x) {
		setLineCount(getLineCount() + x);
	}

	public int getTotalCodeLines() {
		int count = 0;
		for (LangStats ls : getLangStatistics()) {
			count += ls.getCodeLines();
		}
		return count;
	}

	public int getTotalCommentLines() {
		int count = 0;
		for (LangStats ls : getLangStatistics()) {
			count += ls.getCommentLines();
		}
		return count;
	}

	public int getTotalBlankLines() {
		int count = 0;
		for (LangStats ls : getLangStatistics()) {
			count += ls.getBlankLines();
		}
		return count;
	}

	/**
	 * Gets the full name of the branch. For the shortened name, use
	 * {@link HistoryViewer#getBranchName()}
	 * 
	 * @return
	 */
	public String getBranch() {
		return branch;
	}

	public Date getDate() {
		return (Date) theDate.clone();
	}

	public String getLastCommitId() {
		return history;
	}

	/**
	 * Gets the statistics for all languages.
	 * 
	 * @return
	 */
	public LangStats[] getLangStatistics() {
		LangStats[] oldOne = data.getLanguageStats();
		LangStats[] newOne = new LangStats[oldOne.length];

		for (int i = 0; i < newOne.length; i++) {
			newOne[i] = oldOne[i].copy();
		}

		return newOne;

	}

	public float getLangPercent(Language lang) {
		return (float) getLangStats(lang).getnFiles()
				/ (float) data.getHeader().getnFiles();
	}

	/**
	 * Get number of files that use the specified language.
	 * 
	 * @param lang
	 * @return count
	 */
	public LangStats getLangStats(Language lang) {
		return data.getLanguageStatsMutable().containsKey(lang) ? data
				.getLanguageStatsMutable().get(lang).copy()
				: new LangStats(lang);
	}

	/**
	 * @return the name of this branch with refs/heads/ stripped
	 */
	public String getBranchName() {
		return BranchInfo.branchTrimmer(branch);
	}

	public boolean usesCLOCStats() {
		return usesCLOCStats;
	}

	@Override
	public String toString() {

		StringBuilder value = new StringBuilder("Branch: ");
		value.append(getBranchName());
		value.append("\nUses CLOC stats: ");
		value.append(usesCLOCStats);
		value.append("\nSnapshot for date: ");
		value.append(theDate.toString());
		value.append("\nHistory up to commit: ");
		value.append(history);
		value.append("\nFile Count: ");
		value.append(getFileCount());
		value.append("\nLine Count: ");
		value.append(getLineCount());
		value.append("\nLanguage Stats:\n\n");

		value.append("\t");
		value.append(LangStats.getHeader());

		for (LangStats stats : data.getLanguageStats()) {

			value.append("\t");
			value.append(stats.toString(false));

		}
		
		value.append("\t");
		value.append(Util.printNTimes('-', 100));
		value.append("\n");

		final String footer = Util.valueWithSpaces("SUM")
				+ Util.valueWithSpaces(getFileCount())
				+ Util.valueWithSpaces(getTotalCodeLines())
				+ Util.valueWithSpaces(getTotalCommentLines())
				+ Util.valueWithSpaces(getTotalBlankLines());
		
		value.append("\t");
		value.append(footer);
		value.append("\n");

		return value.toString();
	}

}
