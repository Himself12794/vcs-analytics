package com.cisco.dft.sdk.vcs.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.dft.sdk.vcs.common.BranchNotFoundException;
import com.cisco.dft.sdk.vcs.common.Util;
import com.google.common.collect.Lists;

/**
 * Used to get information about different authors who have committed to a
 * remote repo.
 * 
 * @author phwhitin
 *
 */
public final class GitRepo extends Repo {

	/**
	 * Default directory relative to the system temp folder to store the
	 * repository locally so metrics can be pulled from it.
	 */
	private static final String DEFAULT_TEMP_CLONE_DIRECTORY = DEFAULT_DIRECTORY_BASE + "git/";

	private static final Logger LOGGER = LoggerFactory.getLogger(GitRepo.class
			.getSimpleName());

	private final RepoInfo repoInfo = new RepoInfo();

	private final File theDirectory;

	Git theRepo;

	private UsernamePasswordCredentialsProvider cp;

	/**
	 * Links a remote repo with a local version so information can be pulled
	 * from it. When a repo is created, a cached version is stored. This allows
	 * for faster time to get user data for the repository if there is already a
	 * local version. If there is no local copy, the repo automatically clones.
	 * <p>
	 * The repo is cloned bare to only include necessary information.
	 * <p>
	 * Initializing in this way will automatically sync the data if no local
	 * copy is found. If auto-sync is not desired, run with a boolean as false.
	 * 
	 * @param url
	 *            the url to grab the data from
	 * @throws GitAPIException
	 */
	public GitRepo(String url) throws GitAPIException {
		this(url, true);
	}

	public GitRepo(String url, String branch) throws GitAPIException {
		this(url, branch, true);
	}

	/**
	 * Links a remote repo with a local version so information can be pulled
	 * from it. When a repo is created, a cached version is stored. This allows
	 * for faster time to get user data for the repository if there is already a
	 * local version. If there is no local copy, the repo automatically clones.
	 * <p>
	 * The repo is cloned bare to only include necessary information.
	 * 
	 * @param url
	 *            the url to grab the data from
	 * @param generateStats
	 *            whether repositories with local data should automatically sync
	 *            data
	 * @throws GitAPIException
	 */
	public GitRepo(String url, boolean generateStats) throws GitAPIException {
		this(url, null, null, generateStats, null);
	}

	public GitRepo(String url, String branch, boolean generateStats) throws GitAPIException {
		this(url, null, branch, generateStats, null);
	}

	public GitRepo(String url, String branch, boolean generateStats,
			UsernamePasswordCredentialsProvider cp) throws GitAPIException {
		this(url, cp, branch, generateStats, null);
	}

	public GitRepo(String url, UsernamePasswordCredentialsProvider cp) throws GitAPIException {
		this(url, cp, null, true, null);
	}

	/**
	 * Links a remote repo with a local version so information can be pulled
	 * from it. When a repo is created, a cached version is stored. This allows
	 * for faster time to get user data for the repository if there is already a
	 * local version. If there is no local copy, the repo automatically clones.
	 * <p>
	 * The repo is cloned bare to only include necessary information.
	 * 
	 * @param scrubbedUrl
	 *            the url to grab the data from
	 * @param cp
	 *            authentication needed to access private repos, not necessary
	 *            for public repos.
	 * @param generateStatistics
	 *            whether statistics should be generated about the repository
	 * @throws GitAPIException
	 */
	public GitRepo(String url, UsernamePasswordCredentialsProvider cp,
			String branch, boolean generateStatistics, File directory) throws GitAPIException {

		String scrubbedUrl = urlScrubber(url);

		repoInfo.setName(guessName(scrubbedUrl));

		this.theDirectory = getDirectory(scrubbedUrl, directory);

		this.cp = cp != null
				? cp
				: new UsernamePasswordCredentialsProvider("username", "password");

		if (theDirectory.exists()) {

			try {
				initExistingRepo(branch, generateStatistics);
			} catch (IOException e) {

				LOGGER.warn("Temporary data missing or corrupt, attempting to re-clone.");
				LOGGER.debug("Could not reload existing repository", e);

				removeDefunctDirectory(theDirectory);

				try {
					createRepo(scrubbedUrl, branch, generateStatistics);
				} catch (Exception e1) {
					throw new TransportException("Could not connect to remote repository.", e1);
				}
			}

		} else {

			try {
				createRepo(scrubbedUrl, branch, generateStatistics);
			} catch (Exception e1) {
				throw new TransportException("Could not connect to remote repository.", e1);
			}

		}

	}

	/**
	 * Returns a list of the branches in this repository.
	 * 
	 * @return
	 */
	public List<String> getBranches() {

		List<String> branches = Lists.newArrayList();

		try {

			for (Ref ref : theRepo.lsRemote().call()) {

				if (!ref.getName().equals(Constants.HEAD)
						&& ref.getName().contains(Constants.R_HEADS)) {
					branches.add(ref.getName());
				}

			}

		} catch (GitAPIException e) {
			LOGGER.error("An error occured, could not get branches");
			LOGGER.debug("Error occured", e);
		}

		return branches;

	}

	private void initExistingRepo(String branch, boolean value) throws GitAPIException, IOException {

		theRepo = Git.open(theDirectory);
		repoInfo.setRepo(this);

		LOGGER.info("Found cached version of " + repoInfo.getName());

		if (value) {
			validateBranchSync(branch);
		}

	}

	private void validateBranchSync(String branch) throws GitAPIException {

		if (branch == null) {
			sync(true, true);
		} else {
			sync(branch, true, true);
		}

	}

	/**
	 * Syncs the repository with the remote, updating history if necessary.
	 * 
	 * @return
	 */
	public void sync() {
		sync(true, true);
	}

	/**
	 * Synchronizes only the specific branch.
	 * 
	 * @param branch
	 * @throws GitAPIException
	 */
	public void sync(String branch) throws GitAPIException {
		sync(branch, true, true);
	}

	/**
	 * Synchronizes with remote.
	 * 
	 * @param generateStatistics
	 *            whether or not statistics should be generated or updated
	 * @throws GitAPIException
	 */
	public void sync(boolean generateStatistics, boolean useCloc) {

		for (String branch : getBranches()) {
			try {
				sync(branch, generateStatistics, useCloc);
			} catch (GitAPIException e) {
				LOGGER.error("An error occured in synchronizing data", e);
			}
		}

	}

	/**
	 * Synchronizes with remote. Only updates info about the specified branch.
	 * 
	 * @param branchResolved
	 *            the branch about which to update info
	 * @param generateStatistics
	 *            whether or not to update info
	 * @throws GitAPIException
	 */
	public void sync(String branch, boolean generateStatistics, boolean useCloc) throws GitAPIException {

		if (branch == null) {
			sync(generateStatistics, useCloc);
			return;
		}

		String branchResolved = BranchInfo.branchNameResolver(branch);

		if (!getBranches().contains(branchResolved)) { throw new BranchNotFoundException("Branch "
				+ branch + " does not exist."); }

		LOGGER.info(repoInfo.getName() + ": Syncing data for branch "
				+ BranchInfo.branchTrimmer(branch));

		DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream());

		try {

			theRepo.checkout()
					.setName("refs/remotes/origin/" + BranchInfo.branchTrimmer(branch))
					.setCreateBranch(false).call();
			
			boolean flag = theRepo.fetch().setCredentialsProvider(cp)
					.setRemoveDeletedRefs(true).call().getTrackingRefUpdates()
					.isEmpty();

			if (!flag || generateStatistics) {

				updateAuthorInfo(branchResolved, df);
				updateRepoInfo(branchResolved, df, useCloc);
				repoInfo.resolveBranchInfo(getBranches());

			}

		} catch (Exception e) {
			LOGGER.info(
					"There was an error in connection to remote, could not update info",
					e);
		}

		df.close();
	}

	private void updateAuthorInfo(String branch, DiffFormatter df) throws GitAPIException, IOException {

		BranchInfo bi = repoInfo.getBranchInfo(branch);

		LOGGER.info(repoInfo.getName() + ": Updating statistics for branch "
				+ bi.getBranchName());

		RevWalk walk = new RevWalk(theRepo.getRepository());
		ObjectId from = theRepo.getRepository().resolve(Constants.HEAD);
		walk.sort(RevSort.REVERSE);

		if (bi.getMostRecentLoggedCommit() != null) {
			ObjectId to = theRepo.getRepository().resolve(
					bi.getMostRecentLoggedCommit());
			walk.markUninteresting(walk.parseCommit(to));

		}

		walk.markStart(walk.parseCommit(from));

		RevCommit prev = null;

		for (RevCommit rc : walk) {
			String author = rc.getAuthorIdent().getName();
			AuthorInfo ai = bi.getAuthorInfo(author);
			final boolean isMergeCommit = rc.getParentCount() > 1;

			int totalAdditions = 0;
			int totalDeletions = 0;
			int totalFilesAffected = 0;

			if (rc.getParentCount() == 0) {

				int[] results = compareCommits(null, rc, df);
				totalAdditions += results[0];
				totalDeletions += results[1];
				totalFilesAffected += results[2];

			} else {

				for (RevCommit rev : rc.getParents()) {

					int[] results = compareCommits(rev, rc, df);
					totalAdditions += results[0];
					totalDeletions += results[1];
					totalFilesAffected += results[2];

				}

			}

			ai.incrementAdditions(totalAdditions);
			ai.incrementDeletions(totalDeletions);
			ai.add(new AuthorCommit(rc.name(), new Date(((long) rc
					.getCommitTime()) * 1000), totalFilesAffected, totalAdditions, totalDeletions, isMergeCommit, rc
					.getShortMessage()));

			bi.incrementCommitCount(1);

			prev = rc;
		}

		if (prev != null) {
			bi.setMostRecentCommit(prev.getId().name());
		}

		walk.close();
		walk.dispose();

	}

	private void updateRepoInfo(String branch, DiffFormatter df, boolean useCloc) throws IOException {
		repoInfo.getBranchInfo(branch).getHistoryGit(getNewestCommit(branch), df,
				useCloc);
	}

	/**
	 * Compares two commits.
	 * 
	 * 
	 * @param prev
	 * @param curr
	 * @param df
	 * @return array containing info - index reference: 0 = additions, 1 =
	 *         deletions, 2 = files changed, 3 = totalChanges
	 * @throws IOException
	 */
	private int[] compareCommits(RevCommit prev, RevCommit curr, DiffFormatter df) throws IOException {

		ObjectReader reader = theRepo.getRepository().newObjectReader();

		AbstractTreeIterator oldTreeIter = null;
		if (prev == null) {

			oldTreeIter = new EmptyTreeIterator();
			((EmptyTreeIterator) oldTreeIter).reset();

		} else {

			oldTreeIter = new CanonicalTreeParser();
			ObjectId oldTree = prev.getTree();
			((CanonicalTreeParser) oldTreeIter).reset(reader, oldTree);

		}

		AbstractTreeIterator newTreeIter = null;
		if (curr == null) {

			newTreeIter = new EmptyTreeIterator();
			((EmptyTreeIterator) newTreeIter).reset();

		} else {

			newTreeIter = new CanonicalTreeParser();
			ObjectId newTree = curr.getTree();
			((CanonicalTreeParser) newTreeIter).reset(reader, newTree);

		}

		df.setRepository(theRepo.getRepository());
		List<DiffEntry> entries = df.scan(oldTreeIter, newTreeIter);

		int deletions = 0;
		int additions = 0;
		int changedFiles = 0;

		for (DiffEntry entry : entries) {

			changedFiles++;
			FileHeader fh = df.toFileHeader(entry);

			for (HunkHeader hunk : fh.getHunks()) {

				for (Edit edit : hunk.toEditList()) {

					final int deletionLines = (edit.getEndA() - edit
							.getBeginA());
					final int additionLines = (edit.getEndB() - edit
							.getBeginB());

					deletions += deletionLines;
					additions += additionLines;

				}
			}
		}

		return new int[] { additions, deletions, changedFiles };
	}

	private RevCommit getNewestCommit(String branch) {

		RevCommit newest = null;
		try {

			theRepo.checkout()
					.setName("origin/" + BranchInfo.branchTrimmer(branch))
					.setCreateBranch(false).call();
			RevWalk rw = new RevWalk(theRepo.getRepository());
			newest = rw.parseCommit(theRepo.getRepository().resolve(
					Constants.HEAD));
			rw.dispose();
			rw.close();

		} catch (RevisionSyntaxException | IOException | GitAPIException e) {
			LOGGER.error("Could not get most recent commit", e);
		}

		return newest;
	}

	/**
	 * Gets the general information about this repository:
	 * <ol>
	 * <li>Lines of code</li>
	 * <li>Number of files</li>
	 * <li>Language statistics</li>
	 * </ol>
	 * Note: Merge requests do not seem to show any additions or deletions.
	 * 
	 * @return a copy of the statistics object. changing this will not effect
	 *         statistics as a whole.
	 */
	public RepoInfo getRepoStatistics() {
		return repoInfo;
	}

	/**
	 * Tries to clone a repo from remote to local.
	 * 
	 * @param theDirectory
	 * @param uri
	 * @param cp
	 * @throws GitAPIException
	 * @throws IllegalStateException
	 */
	private void createRepo(String remote, String branch, boolean sync) throws IllegalStateException, GitAPIException {

		LOGGER.info("Cloning repo from remote url.");

		CloneCommand command = Git.cloneRepository().setDirectory(theDirectory)
				.setBranch(Util.ifNullDefault(branch, Constants.HEAD)).setURI(remote).setCredentialsProvider(cp);

		theRepo = command.call();

		repoInfo.setRepo(this);

		LOGGER.info("Clone successful.");

		if (sync) {

			validateBranchSync(branch);

		}

	}

	/**
	 * Gets the directory where temporary data is stored.
	 * 
	 * @return
	 */
	public File getDirectory() {
		return theDirectory;
	}

	private File getDirectory(String url, File alternate) {

		if (alternate != null && alternate.exists() && alternate.isDirectory()) { return alternate; }

		UUID name = UUID.nameUUIDFromBytes(url.getBytes());
		return new File(FileUtils.getTempDirectory(), GitRepo.DEFAULT_TEMP_CLONE_DIRECTORY
				+ name.toString());

	}

	/**
	 * JGit seems to have problems using http to clone, so this attempts to
	 * change urls using http to https instead.
	 * 
	 * <br />
	 * <b>Note: </b> This does not attempt to validate the url.
	 * 
	 * @param url
	 * @return the url with https
	 */
	private static String urlScrubber(String url) {
		return url.startsWith("http://") ? url.replace("http://", "https://")
				: url;
	}

	private void removeDefunctDirectory(File dir) {

		try {
			FileUtils.deleteDirectory(dir);
		} catch (IOException e1) {
			LOGGER.warn("An error occured trying to refresh the directory");
			LOGGER.debug("Error is", e1);
		}

	}

	/**
	 * Call this when you are done with this repo. Only call this when you are
	 * done using it, I can't be responsible for incorrect information if this
	 * is used incorrectly. :)
	 */
	public void close() {
		theRepo.getRepository().close();
	}

	@Override
	public String toString() {
		return repoInfo.toString();
	}

}