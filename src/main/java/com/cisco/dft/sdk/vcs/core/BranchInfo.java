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
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
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

import com.cisco.dft.sdk.vcs.common.util.CodeSniffer;
import com.cisco.dft.sdk.vcs.common.util.CodeSniffer.Language;
import com.cisco.dft.sdk.vcs.common.util.Util;
import com.cisco.dft.sdk.vcs.core.ClocData.Header;
import com.cisco.dft.sdk.vcs.core.ClocData.LangStats;
import com.cisco.dft.sdk.vcs.core.util.SortMethod;
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
			.getLogger(BranchInfo.class.getSimpleName());

	private String mostRecentLoggedCommit;

	private final Map<String, AuthorInfo> authorInfo;

	BranchInfo(Repo theRepo) {
		this("Unknown", theRepo, "", new Date());
	}

	BranchInfo(String branch, Repo theRepo) {
		this(branch, theRepo, "", new Date());
	}

	private BranchInfo(final String branch, Repo theRepo, String id, Date date) {
		this(branch, theRepo, id, date, new HashMap<Language, Integer>(), new HashMap<String, AuthorInfo>(), new ClocData());
	}

	private BranchInfo(final String branch, Repo theRepo, String id, Date date,
			final Map<Language, Integer> languageCount,
			final Map<String, AuthorInfo> authorInfo, ClocData data) {
		super(branch, theRepo, id, date, languageCount, data);
		this.authorInfo = authorInfo;

	}

	void resetInfo() {

		this.theDate = new Date();

		setFileCount(0);
		setLineCount(0);

		data.reset();

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
	 * Generates a snapshot of the repository at the certain date using cloc. If no history
	 * is found before this date, the object returned is empty.
	 * <p>
	 * Note that the Date object counts time down to the millisecond, so make
	 * sure you add an appreciable margin if your date is more general.
	 * 
	 * @param date
	 * @return the history object, never null
	 */
	public HistoryViewer getHistoryForDate(Date date) {
		return getHistoryForDate(date, true);
	}

	/**
	 * Generates a snapshot of the repository at the certain date. If no history
	 * is found before this date, the object returned is empty.
	 * <p>
	 * Note that the Date object counts time down to the millisecond, so make
	 * sure you add an appreciable margin if your date is more general.
	 * <p>
	 * Indicating the that the history information should use cloc does not necessary mean
	 * it will succeed, only that it will try. If cloc is not enabled, this will fail
	 * regardless.
	 * 
	 * @param date
	 * @param useCloc if the statistics should be generated using cloc 
	 * @return the history object, never null
	 */
	public HistoryViewer getHistoryForDate(Date date, boolean useCloc) {

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

		HistoryViewer hv = getHistoryForCommit(temp2.getId(), useCloc);
		hv.setDate(date);
		return hv;

	}

	/**
	 * Looks up the history for the specified commit and generates a snapshot.
	 * 
	 * @param commitId
	 * @return
	 */
	public HistoryViewer getHistoryForCommit(String commitId, boolean useCloc) {

		HistoryViewer hv = new HistoryViewer(branch, theRepo, commitId, new Date());
		DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream());

		try {

			hv = lookupHistoryFor(commitId, df, useCloc);

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
	private HistoryViewer lookupHistoryFor(String commitId, DiffFormatter df, boolean useCloc) throws IOException, GitAPIException {

		@SuppressWarnings("resource")
		Git git = (theRepo instanceof GitRepo) ? ((GitRepo)this.theRepo).theRepo : null; 
		
		if (git == null) { 
			throw new WrongRepositoryStateException("Tried to treat the repository as a GitRepo, when it is not");
		}
		
		
		RevWalk rw = new RevWalk(git.getRepository());
		RevCommit current = rw.parseCommit(git.getRepository().resolve(
				Constants.HEAD));
		RevCommit rc = rw
				.parseCommit(git.getRepository().resolve(commitId));

		rw.close();

		git.checkout().setCreateBranch(false).setName(commitId).call();

		Date date = new Date(rc.getCommitTime() * 1000L);

		BranchInfo hv = new BranchInfo(branch, theRepo, rc.getId().name(), date);

		hv.getHistoryGit(rc, df, useCloc);

		git.checkout().setCreateBranch(false).setName(current.name());
		HistoryViewer history = new HistoryViewer(branch, theRepo, commitId, date);
		history.usesCLOCStats = hv.usesCLOCStats;
		history.data.getLanguageStatsMutable().putAll(
				hv.data.getLanguageStatsMutable());
		history.data.getHeader().imprint(hv.data.getHeader());

		return history;

	}

	void getHistoryGit(RevCommit rc, DiffFormatter df, boolean useCloc) throws IncorrectObjectTypeException, IOException {
		
		@SuppressWarnings("resource")
		Git git = (theRepo instanceof GitRepo) ? ((GitRepo)this.theRepo).theRepo : null; 
		
		if (git == null) { return; }
		
		this.resetInfo();

		if (ClocService.canGetCLOCStats() && useCloc) {
			
			LOGGER.debug("Will use cloc to analyze");

			try {
				ClocData theData = ClocService.getClocStatistics(git
						.getRepository().getWorkTree());
				this.getData().imprint(theData);
				usesCLOCStats = true;
				return;

			} catch (IOException e) {
				usesCLOCStats = false;
				LOGGER.error(
						"Could not use CLOC to gather statistics, defaulting to built-in analysis",
						e);
			}

		} 

		Map<Language, LangStats> langStats = this.getData()
				.getLanguageStatsMutable();

		ObjectReader reader = git.getRepository().newObjectReader();

		EmptyTreeIterator oldTreeIter = new EmptyTreeIterator();
		oldTreeIter.reset();

		CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
		ObjectId newTree = rc.getTree();
		newTreeIter.reset(reader, newTree);

		df.setRepository(git.getRepository());
		List<DiffEntry> entries = df.scan(oldTreeIter, newTreeIter);

		Header header = this.getData().getHeader();

		for (DiffEntry entry : entries) {

			Language lang = CodeSniffer.detectLanguage(entry.getNewPath());

			LangStats langStat = Util.putIfAbsent(langStats, lang,
					new LangStats(lang));

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
	
	public int getCommitCount() {
		int count = 0;
		
		for (AuthorInfo ai : authorInfo.values()) {
			count += ai.getCommitCount();
		}
		
		return count;
	}

	ClocData getData() {
		return data;
	}

	void setMostRecentCommit(String string) {
		this.mostRecentLoggedCommit = string;
	}

	String getMostRecentLoggedCommit() {
		return this.mostRecentLoggedCommit;
	}
	
	public String toString(boolean showCommits) {

		StringBuilder value = new StringBuilder(super.toString());
		value.append("\nCommit Count: ");
		value.append(getCommitCount());
		value.append("/n");

		if (showCommits) {
			value.append("\n");
			value.append(getAuthorStatistics().sort(SortMethod.COMMITS).toString());
		}

		return value.toString();
		
	}

	@Override
	public String toString() {
		return toString(true);
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