package com.cisco.dft.sdk.vcs.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.dft.sdk.vcs.common.CodeSniffer;
import com.cisco.dft.sdk.vcs.common.CodeSniffer.Language;
import com.cisco.dft.sdk.vcs.common.SortMethod;
import com.cisco.dft.sdk.vcs.common.Util;
import com.cisco.dft.sdk.vcs.core.CLOCData.Header;
import com.cisco.dft.sdk.vcs.core.CLOCData.LangStats;
import com.cisco.dft.sdk.vcs.main.App;
import com.cisco.dft.sdk.vcs.main.Cloc;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

/**
 * Class used to hold information about a specific branch in a repository. Also
 * a history viewer class whose date is that of the most recent commit.
 * 
 * @author phwhitin
 *
 */
public class BranchInfo extends HistoryViewer {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BranchInfo.class);

	private int commitCount;

	private String mostRecentLoggedCommit;

	private final Map<String, AuthorInfo> authorInfo;

	BranchInfo(Git theRepo) {
		this("Unknown", theRepo, "", new Date());
	}

	BranchInfo(String branch, Git theRepo) {
		this(branch, theRepo, "", new Date());
	}

	private BranchInfo(final String branch, Git theRepo, String id, Date date) {
		this(branch, theRepo, id, date, new HashMap<Language, Integer>(), new HashMap<String, AuthorInfo>(), new CLOCData());
	}

	private BranchInfo(final String branch, Git theRepo, String id, Date date,
			final Map<Language, Integer> languageCount,
			final Map<String, AuthorInfo> authorInfo, CLOCData data) {
		super(branch, theRepo, id, date, languageCount, data);
		this.authorInfo = authorInfo;

	}

	void resetInfo() {

		for (Language lang : languageCount.keySet()) {
			languageCount.put(lang, 0);
		}

		this.theDate = new Date();

		setFileCount(0);
		setLineCount(0);

		data.reset();

	}

	void incrementCommitCount(int x) {

		commitCount += x;

	}

	public int getCommitCount() {
		return commitCount;
	}

	/**
	 * Gets the statistics that have been logged for this branch. The data is
	 * stored in memory for efficiency, so data may be inaccurate unless
	 * {@link GitRepo#sync()} is run. All data is copied, so there is no danger
	 * in compromising internal information.
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

	/**
	 * Generates a snapshot of the repository at the certain date. If no history
	 * is found before this date, the object returned is empty.
	 * <p>
	 * Note that the Date object counts time down to the millisecond, so make
	 * sure you add an appreciable margin if your date is more general.
	 * 
	 * @param date
	 * @return the history object, never null
	 */
	public HistoryViewer getHistoryForDate(Date date) {

		Date prev = new Date(0L);

		AuthorCommit temp2 = new AuthorCommit();

		for (AuthorInfo ai : authorInfo.values()) {

			ai.limitToDateRange(Range.closed(prev, date));

			List<AuthorCommit> acs = ai.getCommits();

			for (AuthorCommit ac : acs) {

				Date date2 = ac.getTimestamp();
				if (date2.after(prev) && date2.compareTo(date) < 1) {
					temp2 = ac;
					prev = date2;
				}

			}

		}

		HistoryViewer hv = getHistoryForCommit(temp2.getId());
		hv.setDate(date);
		return hv;

	}

	/**
	 * Looks up the history for the specified commit and generates a snapshot.
	 * 
	 * @param commitId
	 * @return
	 */
	public HistoryViewer getHistoryForCommit(String commitId) {

		HistoryViewer hv = new HistoryViewer(branch, theRepo, commitId, new Date());
		DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream());

		try {

			hv = lookupHistoryFor(commitId, df);

		} catch (Exception e) {
			LOGGER.error("Could not find history for commit id " + commitId, e);
		} finally {
			df.close();
		}

		return hv;

	}

	/**
	 * Generates a snapshot of the repository at this commit. If the commit id
	 * is invalid, it returns an empty data object.
	 * 
	 * @param commitId
	 * @return data object containing information for this commit. This never
	 *         returns null.
	 * @throws IOException
	 * @throws MissingObjectException
	 * @throws CorruptObjectException
	 * @throws GitAPIException
	 * @throws CheckoutConflictException
	 * @throws InvalidRefNameException
	 * @throws RefNotFoundException
	 * @throws RefAlreadyExistsException
	 */
	private HistoryViewer lookupHistoryFor(String commitId, DiffFormatter df) throws IOException, GitAPIException {

		RevWalk rw = new RevWalk(theRepo.getRepository());
		RevCommit current = rw.parseCommit(theRepo.getRepository().resolve(
				Constants.HEAD));
		RevCommit rc = rw
				.parseCommit(theRepo.getRepository().resolve(commitId));

		rw.close();

		theRepo.checkout().setCreateBranch(false).setName(commitId).call();

		Date date = new Date(rc.getCommitTime() * 1000L);

		BranchInfo hv = new BranchInfo(branch, theRepo, rc.getId().name(), date);

		hv.getHistory(rc, df);

		theRepo.checkout().setCreateBranch(false).setName(current.name());
		HistoryViewer history = new HistoryViewer(branch, theRepo, commitId, date);
		history.usesCLOCStats = hv.usesCLOCStats;
		history.data.getLanguageStatsMutable().putAll(
				hv.data.getLanguageStatsMutable());
		history.data.getHeader().imprint(hv.data.getHeader());

		return history;

	}

	void getHistory(RevCommit rc, DiffFormatter df) throws IncorrectObjectTypeException, IOException {

		this.resetInfo();

		if (Cloc.canGetCLOCStats() && App.getConfiguration().shouldUseCloc()) {

			try {
				CLOCData theData = CodeSniffer.getCLOCStatistics(theRepo
						.getRepository().getWorkTree());
				this.getData().imprint(theData);
				usesCLOCStats = true;
				return;

			} catch (IOException e) {
				usesCLOCStats = false;
				LOGGER.error(
						"Could not use CLOC to gather statistics, defaulting to built-in cheap analysis",
						e);
			}

		} else {
			LOGGER.warn("CLOC disabled, using built-in stat analysis.");
		}

		Map<Language, LangStats> langStats = this.getData()
				.getLanguageStatsMutable();

		ObjectReader reader = theRepo.getRepository().newObjectReader();

		EmptyTreeIterator oldTreeIter = new EmptyTreeIterator();
		oldTreeIter.reset();

		CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
		ObjectId newTree = rc.getTree();
		newTreeIter.reset(reader, newTree);

		df.setRepository(theRepo.getRepository());
		List<DiffEntry> entries = df.scan(oldTreeIter, newTreeIter);

		Header header = this.getData().getHeader();

		for (DiffEntry entry : entries) {

			Language lang = CodeSniffer.detectLanguage(entry.getNewPath());

			LangStats langStat = Util.putIfAbsent(langStats, lang,
					new LangStats(lang));

			incrementLanguage(lang, 1);

			incrementFileCount(1);

			langStat.setnFiles(langStat.getnFiles() + 1);

			for (HunkHeader hunk : df.toFileHeader(entry).getHunks()) {

				header.setnLines(header.getnLines() + hunk.getNewLineCount());
				langStat.setCodeLines(langStat.getCodeLines()
						+ hunk.getNewLineCount());
				this.incrementLineCount(hunk.getNewLineCount());

			}

		}

	}

	CLOCData getData() {
		return data;
	}

	void setMostRecentCommit(String string) {
		this.mostRecentLoggedCommit = string;
	}

	String getMostRecentLoggedCommit() {
		return this.mostRecentLoggedCommit;
	}

	@Override
	public String toString() {

		StringBuilder value = new StringBuilder(super.toString());

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
		return branch.replace(Constants.R_HEADS, "");
	}

	/**
	 * Adds the "refs/heads/" prefix to branch names.
	 * 
	 * @param branch
	 * @return the fresh branch name
	 */
	public static String branchAdder(String branch) {
		return Constants.R_HEADS + branch;
	}

	/**
	 * Ensures the string is prefixed by "refs/heads/"
	 * 
	 * @param branch
	 * @return
	 */
	static String branchNameResolver(String branch) {

		String value = branch == null ? "" : branch;
		if (!value.contains(Constants.R_HEADS)) {
			value = branchAdder(value);
		}

		return value;

	}

}