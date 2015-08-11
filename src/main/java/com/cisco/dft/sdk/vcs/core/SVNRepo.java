package com.cisco.dft.sdk.vcs.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

import com.cisco.dft.sdk.vcs.util.CodeSniffer;
import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;
import com.cisco.dft.sdk.vcs.util.Util;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;

/**
 * SVN Repositories use directories, instead of references, for branches.
 *
 * @author phwhitin
 */
public class SVNRepo extends Repo {

	private static final String LOG_FILE = "logs/commitLog.json";

	private static final String INDEX = "Index: ";

	public static final String TRUNK = "trunk";

	private static final Logger LOGGER = LoggerFactory.getLogger("SVNRepo");

	/**
	 * Default directory relative to the system temp folder to store the
	 * repository locally so metrics can be pulled from it.
	 */
	private static final String DEFAULT_TEMP_CLONE_DIRECTORY = DEFAULT_DIRECTORY_BASE + "svn/";

	private final File theDirectory;

	private final File commitLog;

	protected final SVNRepository theRepo;

	private final ISVNAuthenticationManager authManager;

	private final SVNUpdateClient updateClient;

	@SuppressWarnings("unused")
	private final String url;

	public SVNRepo(final String url) throws SVNException {
		this(url, null, "username", "password", true, true);
	}

	public SVNRepo(final String url, final String branch) throws SVNException {
		this(url, branch, "username", "password", true, true);
	}

	public SVNRepo(final String url, final String branch, final String username,
			final String password, final boolean langStats, final boolean doStats) throws SVNException {

		DAVRepositoryFactory.setup();

		final String resolvedBranch = branch == null ? TRUNK : branch;
		this.url = url;
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
		commitLog = new File(theDirectory, LOG_FILE);
		try {
			FileUtils.forceMkdir(new File(theDirectory, "logs/"));
			if (!commitLog.exists()) {
				commitLog.createNewFile();
			}
		} catch (final IOException e) {
			LOGGER.debug("Error occurred in creating log file", e);
		}

		if (doStats) {
			sync(resolvedBranch, langStats);
		}

	}

	private void addCommitToJsonLog(final Commit commit) {

		final ObjectMapper mapper = new ObjectMapper();

		final List<Commit> commits = getLoggedCommits();

		for (final Commit c : commits) {
			if (c.isTheSame(commit)) { return; }
		}

		commits.add(commit);

		try {
			mapper.writeValue(commitLog, commits);
		} catch (final Exception e) {
			LOGGER.debug("Error occurred during saving to log file", e);
		}
	}

	private void checkoutBranch(final String branch) throws SVNException {

		final File exportPath = new File(theDirectory, "/files/");

		FileUtils.deleteQuietly(exportPath);
		final SVNURL newUrl = theRepo.getLocation().appendPath(branch, true);
		LOGGER.info("Exporting repo to perform language analysis, this may take some time.");
		LOGGER.debug("Exporting from url: {}", newUrl.getPath());
		updateClient.doExport(newUrl, exportPath, SVNRevision.HEAD, SVNRevision.HEAD, null, true,
				SVNDepth.INFINITY);
		LOGGER.debug("exported");

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
				} else if (line.startsWith(INDEX)) {
					final Language lang = CodeSniffer.detectLanguage(line.replace(INDEX, ""));
					Util.incrementInMap(diff.langStats, lang, 1);
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

	private Commit getCommit(final long id) {

		for (final Commit commit : getLoggedCommits()) {
			if (commit.getId().equals(String.valueOf(id))) { return commit; }
		}

		return null;

	}

	private List<Commit> getLoggedCommits() {

		final ObjectMapper mapper = new ObjectMapper();
		final TypeReference<List<Commit>> ref = new TypeReference<List<Commit>>() {
		};
		List<Commit> commits = Lists.newArrayList();

		try {
			commits = mapper.readValue(commitLog, ref);
		} catch (final Exception e) {
			LOGGER.trace("Doesn't exist yet", e);
		}

		return commits;

	}

	@SuppressWarnings("unused")
	private SVNRevision getMostRecentRevFromLog() {
		long mostRecent = 0L;
		for (final Commit commit : getLoggedCommits()) {
			final long curr = Long.valueOf(commit.getId());
			if (curr > mostRecent) {
				mostRecent = curr;
			}
		}
		return s(mostRecent);
	}

	@Override
	public void sync() {
		sync(true);
	}

	public void sync(final boolean doLangStats) {
		try {
			sync(TRUNK, doLangStats);
		} catch (final Exception e) {
			LOGGER.debug("Directory " + TRUNK + " does not exist", e);
		}
	}

	/**
	 * Adds information about the specified branch.
	 *
	 * @param branch
	 * @throws SVNException
	 *             if the directory does not exist
	 */
	public void sync(final String branch, final boolean doLangStats) throws SVNException {
		final Range<Date> range = Range.all();
		sync(branch, doLangStats, range);
	}

	public void sync(final String branch, final boolean doLangStats, final Range<Date> range) throws SVNException {

		LOGGER.info("Syncing data");

		final String temp = TRUNK.equals(branch) ? TRUNK : "branches/" + branch;

		if (doLangStats) {
			checkoutBranch(temp);
		}

		final BranchInfo bi = repoInfo.getBranchInfo(temp);
		bi.resetInfo();

		updateRepoInfo(bi, doLangStats);
		updateAuthorInfo(bi, range);

	}

	@SuppressWarnings({ "unchecked" })
	private void updateAuthorInfo(final BranchInfo bi, final Range<Date> range) throws SVNException {

		LOGGER.info("Getting author information");

		final SVNRevision start = range.hasLowerBound() ? SVNRevision.create(range.lowerEndpoint())
				: SVNRevision.create(0L);
		final SVNRevision end = range.hasUpperBound() ? SVNRevision.create(range.upperEndpoint())
				: SVNRevision.HEAD;

		long currRev = 0L;

		final String temp = bi.getBranch();

		final Collection<SVNLogEntry> logEntries = theRepo.log(new String[] { temp }, null,
				start.getNumber(), end.getNumber(), true, true);

		for (final SVNLogEntry leEntry : logEntries) {

			LOGGER.debug("Revision {}", leEntry.getRevision());
			final long rev = leEntry.getRevision();
			final String author = leEntry.getAuthor();
			final CommitterInfo ai = bi.getAuthorInfo(author, author);
			Commit commit = getCommit(rev);

			if (commit != null) {
				LOGGER.debug("Commit rev {} already exists in log file, skipping.", commit.getId());
			} else {

				final Diff diffs = compareRevisions(s(rev), s(rev - 1));
				currRev++;
				LOGGER.debug("{}/{} entries processed", currRev, logEntries.size());
				commit = new Commit(Long.toString(leEntry.getRevision()), leEntry.getDate(), diffs.changedFiles, diffs.additions, diffs.deletions, false, leEntry
						.getMessage().replace("\n", " "));
				commit.setCommitter(ai.getName());
				addCommitToJsonLog(commit);

			}
			ai.add(commit);
		}
	}

	private void updateRepoInfo(final BranchInfo bi, final boolean update) throws SVNException {

		final String branch = bi.getBranch();

		if (update) {
			LOGGER.info("Updating repo.");
			updateClient.doUpdate(theDirectory, SVNRevision.HEAD, SVNDepth.INFINITY, true, true);
		}

		if (ClocService.canGetCLOCStats()) {

			try {
				final ClocData data = ClocService.getClocStatistics(new File(theDirectory, branch));
				bi.getData().imprint(data);
				bi.usesCLOCStats = true;
			} catch (final IOException e) {
				LOGGER.debug("Cloc stat gathering failed", e);
			}

		}

	}

	private static SVNRevision s(final long rev) {
		return SVNRevision.create(rev);
	}

	private static class Diff {

		private final Map<Language, Integer> langStats = Maps.newHashMap();
		private int additions;
		private int deletions;
		private int changedFiles;

	}

}
