package com.cisco.dft.sdk.vcs.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.Git;

import com.cisco.dft.sdk.vcs.common.CodeSniffer;
import com.cisco.dft.sdk.vcs.common.CodeSniffer.Language;
import com.cisco.dft.sdk.vcs.core.ClocData.LangStats;
import com.google.common.collect.Maps;

public class HistoryViewer {

	protected final Git theRepo;

	private final String history;

	protected boolean usesCLOCStats = false;

	protected Date theDate;

	protected final String branch;

	protected final ClocData data;

	protected final Map<Language, Integer> languageCount;

	HistoryViewer(Git theRepo, String ac, Date date) {
		this("Unknown", theRepo, ac, date);
	}

	protected HistoryViewer(final String branch,
			Git theRepo, String ac, Date date) {
		this(branch, theRepo, ac, date, new HashMap<Language, Integer>(), new ClocData());
	}

	protected HistoryViewer(final String branch, Git theRepo, String ac,
			Date date, final Map<Language, Integer> languageCount, ClocData data) {

		this.branch = branch;
		this.history = ac;
		this.theDate = date;
		this.languageCount = languageCount;
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

	void incrementLanguage(Language lang, int x) {

		if (!languageCount.containsKey(lang)) {

			languageCount.put(lang, x);

		} else {

			languageCount.put(lang, languageCount.get(lang) + x);

		}

	}

	/**
	 * Use {@link HistoryViewer#getLangStatistics()} instead, this information
	 * is not near as precise as the old stat gathering.
	 * 
	 * @deprecated
	 * @return the map of language counts for use with iteration
	 */
	@Deprecated
	public Map<Language, Integer> getLangCountMap() {
		return Maps.newHashMap(languageCount);
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

	/**
	 * Gets the number of files for that are registered as
	 * {@code LangType.PRIMARY}.
	 * <p>
	 * Refer to {@link CodeSniffer.Language} to see which is considered which.
	 * 
	 * @return count
	 */
	public int getPrimaryLangCount() {
		int count = 0;
		for (LangStats langEntry : data.getLanguageStats()) {
			if (langEntry.getLanguage().isPrimary()) {
				count += langEntry.getnFiles();
			}
		}
		return count;
	}

	/**
	 * Gets the number of files for that are registered as
	 * {@code LangType.SECONDARY}.
	 * <p>
	 * Refer to {@link CodeSniffer.Language} to see which is considered which.
	 * 
	 * @return count
	 */
	public int getSecondaryLangCount() {
		int count = 0;
		for (LangStats langEntry : data.getLanguageStats()) {
			if (langEntry.getLanguage().isSecondary()) {
				count += langEntry.getnFiles();
			}
		}
		return count;
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

		final int primaryCount = this.getPrimaryLangCount();
		StringBuilder primary = new StringBuilder("\tPrimary Language Stats:\n");

		primary.append("\tCount: ");
		primary.append(primaryCount);
		primary.append("\n");

		final int secondaryCount = this.getSecondaryLangCount();
		StringBuilder secondary = new StringBuilder("\tSecondary Language Stats:\n");

		secondary.append("\tCount: ");
		secondary.append(secondaryCount);
		secondary.append("\n");

		for (LangStats stats : this.data.getLanguageStats()) {

			if (stats.getLanguage().isPrimary()) {

				primary.append(getOutput(stats));
				primary.append("\n");

			} else {

				secondary.append(getOutput(stats));
				secondary.append("\n");

			}

		}

		value.append(primary.toString());
		value.append("\n");
		value.append(secondary.toString());

		return value.toString();
	}

	private String getOutput(LangStats stats) {
		return "\t  "
				+ stats.getLanguage().name()
				+ ": \n\t\tcount: "
				+ stats.getnFiles()
				+ "\n\t\tpercentage: "
				+ String.format("%.2f",
						getLangPercent(stats.getLanguage()) * 100) + "%";
	}

}
