package com.cisco.dft.sdk.vcs.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
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
import com.cisco.dft.sdk.vcs.util.Util;
import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;
import com.google.common.collect.Maps;

/**
 * SVN Repositories use directories, instead of references, for branches.
 *
 * @author phwhitin
 * @deprecated This is incomplete, and only provides cloc data, and number of
 *             commits by author.
 */
public class SVNRepo extends Repo {
	
	private static final String INDEX = "Index: ";

	public static final String TRUNK = "trunk";

	private static final Logger LOGGER = LoggerFactory.getLogger("SVNRepo");

	/**
	 * Default directory relative to the system temp folder to store the
	 * repository locally so metrics can be pulled from it.
	 */
	private static final String DEFAULT_TEMP_CLONE_DIRECTORY = DEFAULT_DIRECTORY_BASE + "svn/";

	private final File theDirectory;

	protected final SVNRepository theRepo;

	private final ISVNAuthenticationManager authManager;

	@SuppressWarnings("unused")
	private final String url;

	public SVNRepo(final String url) throws SVNException {
		this(url, null, "username", "password");
	}

	public SVNRepo(final String url, final String branch) throws SVNException {
		this(url, branch, "username", "password");
	}

	public SVNRepo(final String url, final String branch, final String username,
			final String password) throws SVNException {

		DAVRepositoryFactory.setup();

		this.url = url;
		theRepo = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
		authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
		theRepo.setAuthenticationManager(authManager);
		repoInfo.setName(guessName(url));
		theDirectory = new File(FileUtils.getTempDirectory(), DEFAULT_TEMP_CLONE_DIRECTORY
				+ theRepo.getRepositoryUUID(true));

		final SVNClientManager ourClientManager = SVNClientManager.newInstance();
		final long latestRevision = theRepo.getLatestRevision();

		ourClientManager.setAuthenticationManager(authManager);

		LOGGER.debug("exporting...");
		
		final SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
		updateClient.setIgnoreExternals(false);
		updateClient.doExport(theRepo.getLocation(), theDirectory,
				SVNRevision.create(latestRevision), SVNRevision.create(latestRevision), null, true,
				SVNDepth.INFINITY);
		
		LOGGER.debug("exported");

		sync(branch == null ? TRUNK : branch);

	}

	private Diff compareRevisions(final SVNRevision rev1, final SVNRevision rev2) throws SVNException {
		
		Diff diff = new Diff();

		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			
			doDiff(rev1, rev2, baos);
			
			int filesChanged = 0;
			int additions = 0;
			int deletions = 0;
			
			String[] lines = baos.toString().split("\n");
			
			for (int i = 0; i < lines.length ; i++) {
				
				final String line = lines[i];
				
				if (line.startsWith("---")) { filesChanged++; }
				else if (line.startsWith(INDEX)) {
					Language lang = CodeSniffer.detectLanguage(line.replace(INDEX, ""));
					Util.incrementInMap(diff.langStats, lang, 1);
				}
				else if (line.startsWith("+")) { additions++; }
				else if (line.startsWith("-")) { deletions++; }

			}
			
			diff.additions = additions;
			diff.deletions = deletions;
			diff.changedFiles = filesChanged;
			
			return diff;
			
		} catch (IOException e) {
			LOGGER.trace("Could not close stream", e);
			return diff;
		} 
	}

	private void doDiff(final SVNRevision rev1, final SVNRevision rev2, final OutputStream baos) throws SVNException {
		
		final SVNDiffClient diffs = new SVNDiffClient(authManager, null);
		diffs.doDiff(theRepo.getLocation(), rev1, rev1, rev2, SVNDepth.INFINITY, true, baos);
		 
	}

	@Override
	public void sync() {
		try {
			sync(TRUNK);
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
	public void sync(final String branch) throws SVNException {
		
		LOGGER.info("Syncing data");

		final String temp = TRUNK.equals(branch) ? TRUNK : "branches/" + branch;

		final BranchInfo bi = repoInfo.getBranchInfo(temp);
		bi.resetInfo();

		updateRepoInfo(bi);
		updateAuthorInfo(bi);

	}

	@SuppressWarnings({ "unchecked" })
	private void updateAuthorInfo(final BranchInfo bi) throws SVNException {
		
		LOGGER.info("Getting author information");

		final long startRevision = 0L;
		
		long currRev = 0L;

		final String temp = bi.getBranch();

		final Collection<SVNLogEntry> logEntries = theRepo.log(new String[] { temp }, null,
				startRevision, theRepo.getLatestRevision(), true, true);

		for (final SVNLogEntry leEntry : logEntries) {
			LOGGER.debug("Revision {}", leEntry.getRevision());
			final long rev = leEntry.getRevision();
			final String author = leEntry.getAuthor();
			final AuthorInfo ai = bi.getAuthorInfo(author);

			Diff diffs = compareRevisions(s(rev), s(rev - 1));
			
			currRev++;
			LOGGER.debug("{}/{} entries processed", currRev, logEntries.size());

			ai.add(new AuthorCommit(Long.toString(leEntry.getRevision()), leEntry.getDate(), diffs.changedFiles, diffs.additions, diffs.deletions, false, leEntry
					.getMessage().replace("\n", " ")));
			
			//bi.getData().
		}
	}

	private void updateRepoInfo(final BranchInfo bi) throws SVNException {

		final String branch = bi.getBranch();

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

	private static SVNRevision s(long rev) {
		return SVNRevision.create(rev);
	}
	
	private class Diff {
		
		private final Map<Language, Integer> langStats = Maps.newHashMap();
		private int additions;
		private int deletions;
		private int changedFiles;
		
	}

}
