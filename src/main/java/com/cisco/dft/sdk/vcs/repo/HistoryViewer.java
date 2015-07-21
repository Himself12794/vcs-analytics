package com.cisco.dft.sdk.vcs.repo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jgit.api.Git;

import com.cisco.dft.sdk.vcs.util.CodeSniffer;
import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;
import com.google.common.collect.Maps;

public class HistoryViewer {

	protected int fileCount;

	protected int lineCount;

	protected final Git theRepo;

	private final String history;

	private Date theDate;

	protected final String branch;

	protected final Map<Language, Integer> languageCount;

	HistoryViewer(Git theRepo, String ac, Date date) {
		this("Unknown", theRepo, ac, date);
	}

	HistoryViewer(final String branch, Git theRepo, String ac, Date date) {
		this(0, 0, branch, theRepo, ac, date);
	}

	protected HistoryViewer(int fileCount, int lineCount, final String branch,
			Git theRepo, String ac, Date date) {
		this(fileCount, lineCount, branch, theRepo, ac, date, new HashMap<Language, Integer>());
	}

	protected HistoryViewer(int fileCount, int lineCount, final String branch,
			Git theRepo, String ac, Date date,
			final Map<Language, Integer> languageCount) {

		this.fileCount = fileCount;
		this.lineCount = lineCount;
		this.branch = branch;
		this.history = ac;
		this.theDate = date;
		this.languageCount = languageCount;
		this.theRepo = theRepo;

	}

	protected void setDate(Date date) {
		theDate = date;
	}

	public int getFileCount() {
		return fileCount;
	}

	protected void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}

	public int getLineCount() {
		return lineCount;
	}

	protected void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}

	void incrementFileCount(int x) {
		fileCount += x;
	}

	void incrementLineCount(int x) {
		lineCount += x;
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
	 * @return the map of language counts for use with iteration
	 */
	public Map<Language, Integer> getLangCountMap() {
		return Maps.newHashMap(languageCount);
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
		for (Entry<Language, Integer> langEntry : languageCount.entrySet()) {
			if (langEntry.getKey().isPrimary()) {
				count += langEntry.getValue();
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
		for (Entry<Language, Integer> langEntry : languageCount.entrySet()) {
			if (langEntry.getKey().isSecondary()) {
				count += langEntry.getValue();
			}
		}
		return count;
	}

	/**
	 * Get the percentage of the repo that is made up of this language.
	 * 
	 * @param lang
	 *            lanugage to look for
	 * @return percentage
	 */
	public float getLangPercent(Language lang) {
		return languageCount.containsKey(lang) ? languageCount.get(lang)
				.floatValue() / fileCount : 0.0F;
	}

	/**
	 * Get number of files that use the specified language.
	 * 
	 * @param lang
	 * @return count
	 */
	public int getLangCount(Language lang) {
		return languageCount.containsKey(lang) ? languageCount.get(lang) : 0;
	}

	/**
	 * @return the name of this branch with refs/heads/ stripped
	 */
	public String getBranchName() {
		return BranchInfo.branchTrimmer(branch);
	}

	@Override
	public String toString() {

		StringBuilder value = new StringBuilder("Branch: ");
		value.append(getBranchName());
		value.append("\nSnapshot for date: ");
		value.append(theDate.toString());
		value.append("\nHistory up to commit: ");
		value.append(history);
		value.append("\nFile Count: ");
		value.append(fileCount);
		value.append("\nLine Count: ");
		value.append(lineCount);
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

		for (Entry<Language, Integer> entry : this.languageCount.entrySet()) {

			if (entry.getKey().isPrimary()) {

				primary.append("\t  "
						+ entry.getKey().name()
						+ ": \n\t\tcount: "
						+ entry.getValue()
						+ "\n\t\tpercentage: "
						+ String.format("%.1f",
								(entry.getValue().floatValue() * 100)
										/ primaryCount) + "%");
				primary.append("\n");

			} else {

				secondary.append("\t  "
						+ entry.getKey().name()
						+ ": \n\t\tcount: "
						+ entry.getValue()
						+ "\n\t\tpercentage: "
						+ String.format("%.1f",
								(entry.getValue().floatValue() * 100)
										/ secondaryCount) + "%");
				secondary.append("\n");

			}

		}

		value.append(primary.toString());
		value.append("\n");
		value.append(secondary.toString());

		return value.toString();
	}

}
