package com.pwhiting.sdk.vcs.core;

import java.util.Date;

import com.pwhiting.util.ClocData;
import com.pwhiting.util.ClocData.LangStats;
import com.pwhiting.util.CodeSniffer.Language;
import com.pwhiting.util.Util;

/**
 * Class used as base.
 * 
 * @author phwhitin
 *
 */
public class HistoryViewer {

	protected final Repo theRepo;

	private final String history;

	boolean usesCLOCStats = false;

	protected Date theDate;

	protected final String branch;

	protected final ClocData data;

	HistoryViewer(final Repo theRepo, final String ac, final Date date) {
		this("Unknown", theRepo, ac, date);
	}

	protected HistoryViewer(final String branch, final Repo theRepo, final String ac,
			final Date date) {
		this(branch, theRepo, ac, date, new ClocData());
	}

	protected HistoryViewer(final String branch, final Repo theRepo, final String ac,
			final Date date, final ClocData data) {

		this.branch = branch;
		history = ac;
		theDate = date;
		this.theRepo = theRepo;
		this.data = data;

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

	/**
	 * @return the name of this branch with refs/heads/ stripped
	 */
	public String getBranchName() {
		return BranchInfo.branchTrimmer(branch);
	}

	public Date getDate() {
		return (Date) theDate.clone();
	}

	public int getFileCount() {
		return data.getHeader().getnFiles();
	}

	public float getLangPercent(final Language lang) {
		return (float) data.getLangStats(lang).getnFiles() / (float) data.getHeader().getnFiles();
	}

	/**
	 * Gets the statistics for all languages.
	 *
	 * @return
	 */
	public LangStats[] getLangStatistics() {
		final com.pwhiting.util.ClocData.LangStats[] oldOne = data.getLanguageStats();
		final LangStats[] newOne = new LangStats[oldOne.length];

		for (int i = 0; i < newOne.length; i++) {
			newOne[i] = oldOne[i].copy();
		}

		return newOne;

	}

	public String getLastCommitId() {
		return history;
	}

	public int getLineCount() {
		return data.getHeader().getnLines();
	}

	public int getTotalBlankLines() {
		int count = 0;
		for (final LangStats ls : getLangStatistics()) {
			count += ls.getBlankLines();
		}
		return count;
	}

	public int getTotalCodeLines() {
		int count = 0;
		for (final LangStats ls : getLangStatistics()) {
			count += ls.getCodeLines();
		}
		return count;
	}

	public int getTotalCommentLines() {
		int count = 0;
		for (final LangStats ls : getLangStatistics()) {
			count += ls.getCommentLines();
		}
		return count;
	}

	void incrementFileCount(final int x) {
		setFileCount(getFileCount() + x);
	}

	void incrementLineCount(final int x) {
		setLineCount(getLineCount() + x);
	}

	protected void setDate(final Date date) {
		theDate = date;
	}

	protected void setFileCount(final int fileCount) {
		data.getHeader().setnFiles(fileCount);
	}

	protected void setLineCount(final int lineCount) {
		data.getHeader().setnLines(lineCount);
	}

	@Override
	public String toString() {

		final StringBuilder value = new StringBuilder("Branch: ");
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

		for (final LangStats stats : data.getLanguageStats()) {

			value.append("\t");
			value.append(stats.toString(false));

		}

		value.append("\t");
		value.append(Util.getNTimes('-', 100, true));

		final String footer = Util.valueWithSpaces("SUM") + Util.valueWithSpaces(getFileCount())
				+ Util.valueWithSpaces(getTotalCodeLines())
				+ Util.valueWithSpaces(getTotalCommentLines())
				+ Util.valueWithSpaces(getTotalBlankLines());

		value.append("\t");
		value.append(footer);
		value.append("\n");

		return value.toString();
	}

	public boolean usesCLOCStats() {
		return usesCLOCStats;
	}

	public LangStats getLangStats(Language lang) {
		return data.getLangStats(lang);
	}

}
