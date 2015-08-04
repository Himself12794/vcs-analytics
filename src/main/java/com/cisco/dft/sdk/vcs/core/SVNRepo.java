package com.cisco.dft.sdk.vcs.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
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

import com.google.common.collect.Lists;

/**
 * SVN Repositories use directories, instead of references, for branches.
 *
 * @author phwhitin
 *
 */
public class SVNRepo extends Repo {

	public static final String TRUNK = "trunk";

	private static final Logger LOGGER = LoggerFactory.getLogger("SVNRepo");

	/**
	 * Default directory relative to the system temp folder to store the
	 * repository locally so metrics can be pulled from it.
	 */
	private static final String DEFAULT_TEMP_CLONE_DIRECTORY = DEFAULT_DIRECTORY_BASE + "svn/";

	private final File theDirectory;

	protected final SVNRepository theRepo;

	private final SVNURL url;

	private final ISVNAuthenticationManager authManager;

	public SVNRepo(final String url) throws SVNException {
		this(url, null, "username", "password");
	}

	public SVNRepo(final String url, final String branch) throws SVNException {
		this(url, branch, "username", "password");
	}

	public SVNRepo(final String url, final String branch, final String username,
			final String password) throws SVNException {

		DAVRepositoryFactory.setup();

		this.url = SVNURL.parseURIEncoded(url);
		theRepo = SVNRepositoryFactory.create(this.url);
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

	@SuppressWarnings("unused")
	private void compareRevisions(final SVNRevision rev1, final SVNRevision rev2, final String... paths) throws SVNException {

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int additions = 0;
		int deletions = 0;

		final SVNURL[] files = new SVNURL[paths.length];

		for (int i = 0; i < files.length; i++) {
			files[i] = SVNURL.parseURIEncoded(theRepo.getLocation() + paths[i]);
		}

		doDiff(rev1, rev2, baos, files);

		for (final String line : baos.toString().split("\n")) {
			final Matcher m = Pattern.compile("@@(.*?)@@").matcher(line);
			while (m.find()) {
				System.out.println(m.group(0));
				final String values[] = m.group(0).split(" ");

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

	}

	private void doDiff(final SVNRevision rev1, final SVNRevision rev2, final OutputStream baos, final SVNURL... urls) throws SVNException {
		final SVNDiffClient diffs = new SVNDiffClient(authManager, null);
		for (final SVNURL url : urls) {
			final SvnDiff diff = diffs.getOperationsFactory().createDiff();
			diff.setDiffGenerator(diffs.getDiffGenerator());
			diff.setSources(SvnTarget.fromURL(url, rev1), SvnTarget.fromURL(url, rev2));
			diff.setDepth(SVNDepth.INFINITY);
			diff.setIgnoreAncestry(true);
			diff.setOutput(baos);
			diff.setApplicalbeChangelists(null);
			diff.setShowCopiesAsAdds(true);
			diff.setUseGitDiffFormat(false);
			diff.run();
		}
	}

	/**
	 * Don't use this, it only returns the head path. You'll have to manually
	 * indicate branches, since revisions are updates to the repositories as a
	 * whole, and not restricted to branches. Branches generally are just
	 * changes in the folder branches, however there is no definitive way to
	 * know when the branch identifier stops, and actual code starts.
	 *
	 * @deprecated Because of how branches work in SVN, it's not trivial to get
	 *             a list of branches
	 */
	@Override
	@Deprecated
	public List<String> getBranches() {
		return Lists.newArrayList(TRUNK);
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

			final List<String> paths = Lists.newArrayList();

			for (final Entry<String, SVNLogEntryPath> e : leEntry.getChangedPaths().entrySet()) {
				if (e.getValue().getKind() == SVNNodeKind.FILE) {
					paths.add(e.getValue().getPath());
				}
			}

			final String author = leEntry.getAuthor();
			final AuthorInfo ai = bi.getAuthorInfo(author);

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

	public static String[] getBranchPaths(final String branch, final String... paths) {

		final List<String> matches = Lists.newArrayList();

		for (final String path : paths) {

			if (path.startsWith("/" + branch)) {
				System.out.println(path);
				matches.add(path);
			}

		}

		return matches.toArray(new String[matches.size()]);

	}

}
