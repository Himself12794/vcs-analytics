package com.cisco.dft.sdk.vcs.repo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.cisco.dft.sdk.vcs.util.CodeSniffer;
import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;
import com.cisco.dft.sdk.vcs.util.SortMethod;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Class used to hold information about a specific branch in a repository.
 * 
 * @author phwhitin
 *
 */
public class BranchInfo {

	private int fileCount;

	private int lineCount;

	private int commitCount;

	private String mostRecentLoggedCommit;

	private final String branch;

	private final Map<Language, Integer> languageCount;

	private final Map<String, AuthorInfo> authorInfo;

	BranchInfo() {
		this("Unknown");
	}

	BranchInfo(final String branch) {
		this(0, 0, branch);
	}

	private BranchInfo(int fileCount, int lineCount, final String branch) {
		this(fileCount, lineCount, branch, new HashMap<Language, Integer>(), new HashMap<String, AuthorInfo>());
	}

	private BranchInfo(int fileCount, int lineCount, final String branch,
			final Map<Language, Integer> languageCount,
			final Map<String, AuthorInfo> authorInfo) {

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

	void incrementCommitCount(int x) {

		commitCount += x;

	}

	public int getCommitCount() {
		return commitCount;
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
	 * Get number of files that us the specified language.
	 * 
	 * @param lang
	 * @return count
	 */
	public int getLangCount(Language lang) {
		return languageCount.containsKey(lang) ? languageCount.get(lang) : 0;
	}

	/**
	 * @return number of files
	 */
	public int getFileCount() {
		return fileCount;
	}

	private void setFileCount(int count) {
		fileCount = count;
	}

	void incrementFileCount(int x) {
		fileCount += x;
	}

	/**
	 * @return lines of code on this branch
	 */
	public int getLineCount() {
		return lineCount;
	}

	private void setLineCount(int count) {
		lineCount = count;
	}

	void incrementLineCount(int x) {
		lineCount += x;
	}

	/**
	 * @return the name of this branch
	 */
	public String getName() {
		return branchTrimmer(branch);
	}

	/**
	 * @return the map of language counts for use with iteration
	 */
	public Map<Language, Integer> getLangCountMap() {
		return Maps.newHashMap(languageCount);
	}

	/**
	 * Gets the statistics that have been logged for this branch. The data is
	 * stored in memory for efficiency, so data may be inaccurate unless
	 * {@link GitRepo#sync()} is run.
	 * 
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

		} else {
			ai = authorInfo.get(author);
		}

		return ai;

	}

	void setMostRecentCommit(String string) {
		this.mostRecentLoggedCommit = string;
	}

	String getMostRecentLoggedCommit() {
		return this.mostRecentLoggedCommit;
	}

	@Override
	public String toString() {

		StringBuilder value = new StringBuilder("Branch: ");
		value.append(getName());
		value.append("\nFile Count: ");
		value.append(fileCount);
		value.append("\nTotal Commits: ");
		value.append(getCommitCount());
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

		value.append("\n");
		value.append(getAuthorStatistics().sort(SortMethod.COMMITS).toString());

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

	/**
	 * Adds the "refs/heads/" prefix to branch names.
	 * 
	 * @param branch
	 * @return the fresh branch name
	 */
	public static String branchAdder(String branch) {
		return "refs/heads/" + branch;
	}

	/**
	 * Ensures the string is prefixed by "refs/heads/"
	 * 
	 * @param branch
	 * @return
	 */
	static String branchNameResolver(String branch) {

		String value = branch;

		if (!branch.contains("refs/heads/")) {
			value = branchAdder(branch);
		}

		return value;

	}

}