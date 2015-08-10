package com.cisco.dft.sdk.vcs.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
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
import org.tmatesoft.svn.core.wc2.SvnDiff;
import org.tmatesoft.svn.core.wc2.SvnTarget;

/**
 * SVN Repositories use directories, instead of references, for branches.
 *
 * @author phwhitin
 * @deprecated This is incomplete, and only provides cloc data, and number of
 *             commits by author.
 */
@Deprecated
public class SVNRepo extends Repo {
	
	private static final boolean INCOMPLETE = true;

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

		final SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
		updateClient.setIgnoreExternals(false);
		updateClient.doExport(theRepo.getLocation(), theDirectory,
				SVNRevision.create(latestRevision), SVNRevision.create(latestRevision), null, true,
				SVNDepth.INFINITY);

		sync(branch == null ? TRUNK : branch);

	}

	private void compareRevisions(final SVNRevision rev1, final SVNRevision rev2, final File... files) throws SVNException {

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		for (File file : files) {
			doDiff(rev1, rev2, baos, file);

			int additions = 0;
			int deletions = 0;

			for (final String line : baos.toString().split("\n")) {
				final Matcher m = Pattern.compile("@@(.*?)@@").matcher(line);
				while (m.find()) {
					System.out.println(m.group(0));
					final String[] values = m.group(0).split(" ");

					final String[] deletionIndent = values[1].split(",");
					final int num0 = Math.abs(Integer.valueOf(deletionIndent[0]));
					final int num1 = Integer.valueOf(deletionIndent[1]);
					System.out.println("found -" + num0 + "," + num1);

					final int linesDeleted = num0 + num1 + 1;
					deletions += linesDeleted;

					final String[] additionIndent = values[2].split(",");
					final int num2 = Math.abs(Integer.valueOf(additionIndent[0]));
					final int num3 = Integer.valueOf(additionIndent[1]);
					System.out.println("found +" + num2 + "," + num3);

					final int linesAdded = num3 + num2 + 1;
					additions += linesAdded;
				}
			}

			System.out.println("Additions: " + additions + ", deletions: " + deletions);
			baos.reset();
		}
	}

	private void doDiff(final SVNRevision rev1, final SVNRevision rev2, final OutputStream baos, final File file) throws SVNException {
		final SVNDiffClient diffs = new SVNDiffClient(authManager, null);
		diffs.doDiff(file, rev1, file, rev2, SVNDepth.INFINITY, true, baos, null);
		/*
		 * for (final File file : files) { final SvnDiff diff =
		 * diffs.getOperationsFactory().createDiff();
		 * diff.setDiffGenerator(diffs.getDiffGenerator());
		 * diff.setSources(SvnTarget.fromFile(file, rev1),
		 * SvnTarget.fromFile(file, rev2)); diff.setDepth(SVNDepth.INFINITY);
		 * diff.setIgnoreAncestry(true); diff.setOutput(baos);
		 * diff.setApplicalbeChangelists(null); diff.setShowCopiesAsAdds(true);
		 * diff.setUseGitDiffFormat(false); diff.run(); }
		 */
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

		final String temp = TRUNK.equals(branch) ? TRUNK : "branches/" + branch;

		final BranchInfo bi = repoInfo.getBranchInfo(temp);
		bi.resetInfo();

		updateRepoInfo(bi);
		updateAuthorInfo(bi);

	}

	@SuppressWarnings({ "unchecked" })
	private void updateAuthorInfo(final BranchInfo bi) throws SVNException {

		final long startRevision = 0L;

		final String temp = bi.getBranch();

		final Collection<SVNLogEntry> logEntries = theRepo.log(new String[] { temp }, null,
				startRevision, theRepo.getLatestRevision(), true, true);

		for (final SVNLogEntry leEntry : logEntries) {

			final long rev = leEntry.getRevision();
			final String author = leEntry.getAuthor();
			final AuthorInfo ai = bi.getAuthorInfo(author);
			final SVNLogEntryPath[] logPath = leEntry.getChangedPaths().values()
					.toArray(new SVNLogEntryPath[leEntry.getChangedPaths().values().size()]);

			if (!INCOMPLETE) { compareRevisions(s(rev), s(rev > 1 ? rev - 1 : rev), pathsToFiles(logPath)); }

			ai.add(new AuthorCommit(Long.toString(leEntry.getRevision()), leEntry.getDate(), 0, 0, 0, false, leEntry
					.getMessage().replace("\n", " ")));
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

	private static File[] pathsToFiles(SVNLogEntryPath... paths) {
		File[] files = new File[paths.length];

		for (int i = 0; i < paths.length; i++) {

			files[i] = new File(paths[i].getPath());

		}

		return files;
	}

	private static SVNRevision s(long rev) {
		return SVNRevision.create(rev);
	}

}
