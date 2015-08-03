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

import com.cisco.dft.sdk.vcs.main.ClocService;
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
	private static final String DEFAULT_TEMP_CLONE_DIRECTORY = DEFAULT_DIRECTORY_BASE
			+ "svn/";

	private final File theDirectory;

	protected final SVNRepository theRepo;

	private final SVNURL url;

	private final ISVNAuthenticationManager authManager;

	public SVNRepo(String url) throws SVNException {
		this(url, null, "username", "password");
	}
	
	public SVNRepo(String url, String branch) throws SVNException {
		this(url, branch, "username", "password");
	}

	public SVNRepo(String url, String branch, String username, String password) throws SVNException {

		DAVRepositoryFactory.setup();

		this.url = SVNURL.parseURIEncoded(url);
		theRepo = SVNRepositoryFactory.create(this.url);
		authManager = SVNWCUtil.createDefaultAuthenticationManager(username,
				password);
		theRepo.setAuthenticationManager(authManager);
		repoInfo.setName(guessName(url));
		theDirectory = new File(FileUtils.getTempDirectory(), DEFAULT_TEMP_CLONE_DIRECTORY
				+ theRepo.getRepositoryUUID(true));

		SVNClientManager ourClientManager = SVNClientManager.newInstance();
		long latestRevision = theRepo.getLatestRevision();

		ourClientManager.setAuthenticationManager(authManager);

		SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
		updateClient.setIgnoreExternals(false);
		updateClient.doExport(theRepo.getLocation(), theDirectory,
				SVNRevision.create(latestRevision),
				SVNRevision.create(latestRevision), null, true,
				SVNDepth.INFINITY);

		sync(branch == null ? TRUNK : branch);

	}

	@Override
	public void sync() {
		try {
			sync(TRUNK);
		} catch (Exception e) {
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
	public void sync(String branch) throws SVNException {

		final String temp = TRUNK.equals(branch) ? TRUNK : "branches/" + branch;

		BranchInfo bi = repoInfo.getBranchInfo(temp);
		bi.resetInfo();

		updateRepoInfo(bi);
		updateAuthorInfo(bi);

	}

	private void updateRepoInfo(BranchInfo bi) throws SVNException {

		String branch = bi.getBranch();

		if (ClocService.canGetCLOCStats()) {

			try {
				ClocData data = ClocService
						.getClocStatistics(new File(theDirectory, branch));
				bi.getData().imprint(data);
				bi.usesCLOCStats = true;
			} catch (IOException e) {
				LOGGER.debug("Cloc stat gathering failed", e);
			}

		}

	}

	@SuppressWarnings({ "unchecked" })
	private void updateAuthorInfo(BranchInfo bi) throws SVNException {

		final long startRevision = 0L;

		final String temp = bi.getBranch();

		Collection<SVNLogEntry> logEntries = theRepo.log(new String[] { temp },
				null, startRevision, theRepo.getLatestRevision(), true, true);

		bi.incrementCommitCount(logEntries.size());

		for (SVNLogEntry leEntry : logEntries) {

			List<String> paths = Lists.newArrayList();

			for (Entry<String, SVNLogEntryPath> e : leEntry.getChangedPaths()
					.entrySet()) {
				if (e.getValue().getKind() == SVNNodeKind.FILE) { paths.add(e.getValue().getPath()); }
			}

			String author = leEntry.getAuthor();
			AuthorInfo ai = bi.getAuthorInfo(author);

			ai.add(new AuthorCommit(Long.toString(leEntry.getRevision()), leEntry
					.getDate(), 0, 0, 0, false, leEntry.getMessage().replace(
					"\n", " ")));
		}
	}

	@SuppressWarnings("unused")
	private void compareRevisions(SVNRevision rev1, SVNRevision rev2, String... paths) throws SVNException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int additions = 0;
		int deletions = 0;

		SVNURL[] files = new SVNURL[paths.length];

		for (int i = 0; i < files.length; i++) {
			files[i] = SVNURL.parseURIEncoded(theRepo.getLocation() + paths[i]);
		}

		doDiff(rev1, rev2, baos, files);

		for (String line : baos.toString().split("\n")) {
			Matcher m = Pattern.compile("@@(.*?)@@").matcher(line);
			while (m.find()) {
				System.out.println(m.group(0));
				String values[] = m.group(0).split(" ");

				String[] deletionIndent = values[1].split(",");
				int num0 = Math.abs(Integer.valueOf(deletionIndent[0]));
				int num1 = Integer.valueOf(deletionIndent[1]);
				System.out.println("found -" + num0 + "," + num1);

				int linesDeleted = num0 + num1 + 1;
				deletions += linesDeleted;

				String[] additionIndent = values[2].split(",");
				int num2 = Math.abs(Integer.valueOf(additionIndent[0]));
				int num3 = Integer.valueOf(additionIndent[1]);
				System.out.println("found +" + num2 + "," + num3);

				int linesAdded = num3 + num2 + 1;
				additions += linesAdded;
			}
		}

		System.out.println("Additions: " + additions + ", deletions: "
				+ deletions);

	}
	
	private void doDiff(SVNRevision rev1, SVNRevision rev2, OutputStream baos, SVNURL...urls ) throws SVNException {
		SVNDiffClient diffs = new SVNDiffClient(this.authManager, null);
		for (SVNURL url : urls) {
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

	public static String[] getBranchPaths(String branch, String... paths) {

		List<String> matches = Lists.newArrayList();

		for (String path : paths) {

			if (path.startsWith("/" + branch)) {
				System.out.println(path);
				matches.add(path);
			}

		}

		return matches.toArray(new String[matches.size()]);

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

}
