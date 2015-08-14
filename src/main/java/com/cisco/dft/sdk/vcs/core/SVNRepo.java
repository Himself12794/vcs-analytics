package com.cisco.dft.sdk.vcs.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.cisco.dft.sdk.vcs.core.util.CommitLogger;
import com.cisco.dft.sdk.vcs.util.CodeSniffer;

/**
 * SVN Repositories use directories, instead of references, for branches. Unlike
 * a GitRepo, you must manually synchronize for each branch since there's no
 * telling which directories are considered branches.
 * <p>
 * SVN repos also store information differently, and any branch information only
 * holds revisions that affected that branch, since revisions affect the repo as
 * a whole, not just specific branches.
 *
 * @author phwhitin
 */
public class SVNRepo extends Repo {

	public static final String TRUNK = "trunk";

	private static final Logger LOGGER = LoggerFactory.getLogger("SVNRepo");

	/**
	 * Default directory relative to the system temp folder to store the
	 * repository locally so metrics can be pulled from it.
	 */
	private static final String DEFAULT_TEMP_CLONE_DIRECTORY = DEFAULT_DIRECTORY_BASE + "svn/";

	private static final String DEFAULT_FILE_PATH = "files/";

	private static final String DEFAULT_USERNAME = "username";

	private static final String DEFAULT_PASSWORD = "password";

	private static final boolean AUTOSYNC = true;

	private final ISVNAuthenticationManager authManager;

	private final SVNUpdateClient updateClient;

	private final CommitLogger commitLogger;

	private String currBranch;

	final SVNRepository theRepo;

	/**
	 * Initializes a repo with the given url. Doesn't attempt any verification,
	 * and automatically sync information for trunk if successful.
	 *
	 * @param url
	 * @throws SVNException
	 */
	public SVNRepo(final String url) throws SVNException {
		this(url, null, DEFAULT_USERNAME, DEFAULT_PASSWORD, AUTOSYNC, AUTOSYNC);
	}

	/**
	 * Behaves just like {@link SVNRepo#SVNRepo(String)} except uses the
	 * specified path instead.
	 *
	 * @param url
	 * @param branch
	 * @throws SVNException
	 */
	public SVNRepo(final String url, final String branch) throws SVNException {
		this(url, branch, DEFAULT_USERNAME, DEFAULT_PASSWORD, AUTOSYNC, AUTOSYNC);
	}

	/**
	 * Behaves just like {@link SVNRepo#SVNRepo(String, String)}, but with the
	 * option to not synchronize automatically.
	 *
	 * @param url
	 * @param branch
	 * @param autoSync
	 * @throws SVNException
	 */
	public SVNRepo(final String url, final String branch, final boolean autoSync) throws SVNException {
		this(url, branch, DEFAULT_USERNAME, DEFAULT_PASSWORD, autoSync, autoSync);
	}

	/**
	 * Initializes a SVN repo with the given url, then runs diagnostics on the
	 * given branch, according to the {@code langStats} and {@code doStats}.
	 *
	 * Uses given username and password to authenticate.
	 *
	 * @param url
	 *            location of the repo
	 * @param branch
	 *            initial branch on which to run diagnostics
	 * @param username
	 *            for authentication
	 * @param password
	 *            for authentication
	 * @param langStats
	 *            if the repo should generate langStats
	 * @param doStats
	 *            if the repo should generate author statistics
	 * @throws SVNException
	 *             if an error occurs in initialization
	 */
	public SVNRepo(final String url, final String branch, final String username,
			final String password, final boolean langStats, final boolean doStats) throws SVNException {

		DAVRepositoryFactory.setup();

		currBranch = branch == null ? TRUNK : branch;
		theRepo = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
		authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
		theRepo.setAuthenticationManager(authManager);
		repoInfo.setName(guessName(url));
		theDirectory = new File(FileUtils.getTempDirectory(), DEFAULT_TEMP_CLONE_DIRECTORY
				+ theRepo.getRepositoryUUID(true));

		final SVNClientManager ourClientManager = SVNClientManager.newInstance();

		ourClientManager.setAuthenticationManager(authManager);

		updateClient = ourClientManager.getUpdateClient();
		updateClient.setIgnoreExternals(false);
		commitLogger = getCommitLogger();

		if (langStats && doStats) {
			sync(branch, langStats, doStats);
		}

	}

	private void checkoutBranch(final String branch) throws SVNException {

		final File exportPath = new File(theDirectory, DEFAULT_FILE_PATH);

		FileUtils.deleteQuietly(exportPath);
		final SVNURL newUrl = theRepo.getLocation().appendPath(branch, true);

		LOGGER.info("Exporting repo to perform language analysis, this may take some time.");
		LOGGER.debug("Exporting from url {} to {}", newUrl.getPath(), exportPath.getAbsolutePath());
		updateClient.doExport(newUrl, exportPath, SVNRevision.HEAD, SVNRevision.HEAD, null, true,
				SVNDepth.INFINITY);
		LOGGER.info("Export complete.");

	}

	private Diff compareRevisions(final SVNRevision rev1, final SVNRevision rev2) throws SVNException {

		final Diff diff = new Diff();

		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			doDiff(rev1, rev2, baos);

			int filesChanged = 0;
			int additions = 0;
			int deletions = 0;

			final String[] lines = baos.toString().split("\n");
			
			for (final String line : lines) {
				
				if (line.startsWith("---")) {
					filesChanged++;
				} else if (line.startsWith("+++")) {
					continue;
				} else if (line.startsWith("+")) {
					additions++;
				} else if (line.startsWith("-")) {
					deletions++;
				}

			}

			diff.additions = additions;
			diff.deletions = deletions;
			diff.changedFiles = filesChanged;

			return diff;

		} catch (final IOException e) {
			LOGGER.trace("Could not close stream", e);
			return diff;
		}
	}

	private void doDiff(final SVNRevision rev1, final SVNRevision rev2, final OutputStream baos) throws SVNException {

		final SVNDiffClient diffs = new SVNDiffClient(authManager, null);
		diffs.doDiff(theRepo.getLocation(), rev1, rev1, rev2, SVNDepth.INFINITY, true, baos);

	}

	/**
	 * The current "branch" that exists in temp.
	 *
	 * @return
	 */
	public String getBranch() {
		return currBranch;
	}

	/**
	 * Synchronizes information for the directory trunk. Performs all
	 * diagnostics.
	 * 
	 * @throws SVNException
	 *
	 */
	@Override
	public void sync() {
		try {
			sync(true);
		} catch (Exception e) {
			LOGGER.debug("Error occured in synchronization.", e);
		}
	}

	/**
	 * Syncs for trunk, but with the option to not generate language statistics.
	 *
	 * @param doLangStats
	 * @throws SVNException
	 */
	public void sync(final boolean doLangStats) throws SVNException {
		sync(TRUNK, doLangStats, true);
	}

	/**
	 * Adds information about the specified branch, with the option to disable
	 * either types of information gathering. This is useful if you only want
	 * specific information.
	 *
	 * @param branch
	 * @throws SVNException
	 *             if the directory does not exist
	 */
	public void sync(final String branch, final boolean doLangStats, final boolean doStats) throws SVNException {
		sync(branch, doLangStats, doStats, s(0), SVNRevision.HEAD);
	}

	/**
	 * Works like {@link SVNRepo#sync(String, boolean, boolean)}, except
	 * synchronization of author information can be limited to specific revision
	 * ranges.
	 *
	 * @param branch
	 * @param doLangStats
	 * @param doStats
	 * @param endA
	 * @param endB
	 * @throws SVNException
	 */
	public void sync(final String branch, final boolean doLangStats, final boolean doStats, final SVNRevision endA, final SVNRevision endB) throws SVNException {

		currBranch = branch;

		final String temp = branch == null ? TRUNK : branch;

		final BranchInfo bi = repoInfo.getBranchInfo(temp);
		bi.resetInfo();

		if (doLangStats) {
			updateRepoInfo(bi, doLangStats);
		}

		if (doStats) {
			updateAuthorInfo(bi, endA, endB);
		}

	}

	@SuppressWarnings({ "unchecked" })
	private void updateAuthorInfo(final BranchInfo bi, final SVNRevision endA, final SVNRevision endB) throws SVNException {

		LOGGER.info("Getting author information for branch {}", bi.getBranch());

		final SVNRevision start = endA == null ? s(0L) : endA;
		final SVNRevision end = endB == null ? SVNRevision.HEAD : endB;

		long currRev = 0L;

		final String temp = bi.getBranch();

		final Collection<SVNLogEntry> logEntries = theRepo.log(new String[] { temp }, null,
				start.getNumber(), end.getNumber(), true, true);

		LOGGER.info("Analyzing {} entries.", logEntries.size());

		for (final SVNLogEntry leEntry : logEntries) {

			LOGGER.debug("Revision {}", leEntry.getRevision());
			final long rev = leEntry.getRevision();
			final String author = leEntry.getAuthor();
			final CommitterInfo ai = bi.getAuthorInfo(author, "", author, "");
			Commit commit = commitLogger.getCommit(rev);

			if (commit != null) {
				LOGGER.debug("Commit rev {} already exists in log file, skipping.", commit.getId());
			} else {

				LOGGER.debug("Calculating differences...");
				final Diff diffs = compareRevisions(s(rev - 1), s(rev));

				LOGGER.debug(
						"Differences calculated with {} additions, {} deletions, and {} files changed",
						diffs.additions, diffs.deletions, diffs.changedFiles);

				commit = new Commit(Long.toString(leEntry.getRevision()), leEntry.getDate(), diffs.changedFiles, diffs.additions, diffs.deletions, false, leEntry
						.getMessage().replace("\n", " "));

				commit.setCommitter(ai.getCommitterName());
				commitLogger.addCommitToJsonLog(commit);

			}

			currRev++;
			LOGGER.debug("{}/{} entries processed", currRev, logEntries.size());
			ai.incrementAdditions(commit.getAdditions());
			ai.incrementDeletions(commit.getDeletions());
			ai.add(commit);
		}
	}

	private void updateRepoInfo(final BranchInfo bi, final boolean update) throws SVNException {

		final String branch = bi.getBranch();
		final File exportPath = new File(theDirectory, DEFAULT_FILE_PATH);

		// Checks out the branch for analysis
		checkoutBranch(branch);

		ClocData data;

		LOGGER.info("Getting language statistics for branch {}", branch);

		if (ClocService.canGetCLOCStats()) {

			try {
				data = ClocService.getClocStatistics(exportPath);
				bi.usesCLOCStats = true;
			} catch (final IOException e) {

				data = CodeSniffer.analyzeDirectory(exportPath);
				LOGGER.debug("Cloc stat gathering failed", e);

			}

		} else {
			data = CodeSniffer.analyzeDirectory(exportPath);
			bi.usesCLOCStats = false;
		}

		bi.getData().imprint(data);

	}

	private static SVNRevision s(final long rev) {
		return SVNRevision.create(rev);
	}

	/**
	 * Utility wrapper for difference information.
	 *
	 * @author phwhitin
	 *
	 */
	private static class Diff {

		private int additions;
		private int deletions;
		private int changedFiles;

	}

}
