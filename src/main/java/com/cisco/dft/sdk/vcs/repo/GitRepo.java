package com.cisco.dft.sdk.vcs.repo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
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
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.dft.sdk.vcs.util.CodeSniffer;
import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;
import com.google.common.collect.Lists;

/**
 * Used to get information about different authors who have committed to a remote repo.
 * 
 * @author phwhitin
 *
 */
public final class GitRepo {
	
	/**Default directory relative to the system temp folder to store the repository locally so metrics can be pulled from it*/
	private static final String DEFAULT_TEMP_CLONE_DIRECTORY = "git_analytics/";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GitRepo.class);
	
	private final File theDirectory;
	
	private final String remote;
	
	private final RepoInfo repoInfo = new RepoInfo();
	
	private Git theRepo;
	
	private UsernamePasswordCredentialsProvider cp;

	/**
	 * Links a remote repo with a local version so information can be pulled from it.
	 * When a repo is created, a cached version is stored. This allows for faster
	 * time to get user data for the repository if there is already a local version.
	 * If there is no local copy, the repo automatically clones. 
	 * <p>
	 * The repo is cloned bare to only include necessary information.
	 * <p>
	 * Initializing in this way will automatically sync the data if no local copy is found. 
	 * If auto-sync is not desired, run with a boolean as false. 
	 * 
	 * @param url the url to grab the data from
	 * @throws Exception 
	 */
	public GitRepo(String url) throws TransportException {
		this(url, null, true);
	}
	
	/**
	 * Links a remote repo with a local version so information can be pulled from it.
	 * When a repo is created, a cached version is stored. This allows for faster
	 * time to get user data for the repository if there is already a local version.
	 * If there is no local copy, the repo automatically clones. 
	 * <p>
	 * The repo is cloned bare to only include necessary information. 
	 * 
	 * @param url the url to grab the data from
	 * @param autoSync whether repositories with local data should automatically sync data
	 * @throws Exception 
	 */
	public GitRepo(String url, boolean autoSync) throws TransportException {
		this(url, null, autoSync);
	}
	
	public GitRepo(String url, UsernamePasswordCredentialsProvider cp) throws TransportException {	
		this(url, cp, true);	
	}
	
	/**
	 * Links a remote repo with a local version so information can be pulled from it.
	 * When a repo is created, a cached version is stored. This allows for faster
	 * time to get user data for the repository if there is already a local version.
	 * If there is no local copy, the repo automatically clones. 
	 * <p>
	 * The repo is cloned bare to only include necessary information. 
	 * 
	 * @param url the url to grab the data from
	 * @param cp authentication needed to access private repos, not necessary for public repos.
	 * @param autoSync whether repositories with local data should automatically sync data
	 * @throws Exception 
	 */
	public GitRepo(String url, UsernamePasswordCredentialsProvider cp, boolean autoSync) throws TransportException {
		
		remote = urlScrubber(url);
		
		theDirectory = getDirectory(remote);
		
		if (cp != null) { this.cp = cp; }
		else { this.cp = new UsernamePasswordCredentialsProvider("username", "password"); }
		
		if (theDirectory.exists()) {
			
			try {
				
				theRepo = Git.open(theDirectory);
				if (autoSync) { sync(); }
					
			} catch (Exception e) {
				
				LOGGER.info("Temporary data missing or corrupt, attempting to re-clone.", e);
				
				try {
					FileUtils.deleteDirectory(theDirectory);
				} catch (IOException e1) {
					LOGGER.info("An error occured trying to refresh the directory", e1);
				}
				
				try {
					createRepo();
				} catch (Exception e1) {
					throw new TransportException("Could not connect to local or remote repository.", e1);
				}
			}
			
		} else {
			
			try {
				createRepo();
			} catch (Exception e1) {
				throw new TransportException("Could not connect to local or remote repository.", e1);
			}
			
		}
		
	}
	
	public List<String> getBranches() {
		
		List<String> branches = Lists.newArrayList();
		
		try {
			
			for (Ref ref : theRepo.lsRemote().call()) {
				if (!ref.getName().equals(Constants.HEAD) && ref.getName().contains("refs/heads/")) { branches.add(ref.getName()); }
			}

		} catch (GitAPIException e) {
			LOGGER.error("An error occured, could not set the branch", e);
		} 
		
		return branches;
		
	}
	
	/**
	 * Syncs the repository with the remote, updating history if necessary. 
	 * 
	 * @return
	 */
	public void sync() {
		sync(true);
	}
	
	private void sync(boolean fetch) {
		
		DiffFormatter df = new DiffFormatter( new ByteArrayOutputStream() );
		
		try {
			
			if (fetch) {
				try {
					theRepo.fetch().call();
				} catch (Exception e) {
					LOGGER.info("Either local is up to date, or there was an error in connection to remote", e);
				}
			}
			
			updateAuthorInfo(df);
			
			updateRepoInfo(df);
			
			repoInfo.resolveBranchInfo(getBranches());
			
		} catch (Exception e) {
			
			LOGGER.error("Could not update repo information", e);
			
		}
				
		df.close();
		
	}
	
	private void updateAuthorInfo(DiffFormatter df) throws GitAPIException, IOException {
		
		for (String branch : getBranches()) {
			
			BranchInfo bi = repoInfo.getBranchInfo(branch);
		
			Iterable<RevCommit> refs = theRepo.log().add(theRepo.getRepository().resolve(branch)).setSkip(bi.getMostRecentLoggedCommit()).call();
			
			RevCommit prev = null;
			
			for (RevCommit rc : refs) {
				
				bi.incrementMostRecentCommit(1);
				
				String author = rc.getAuthorIdent().getName();
				AuthorInfo ai = bi.getAuthorInfo(author);
				
				if (prev == null) {
					prev = rc;
					continue;
				}
				
				int[] results = compareCommits(prev, rc, df);
				
				ai.incrementAdditions(results[0]);
				ai.incrementDeletions(results[1]);
				ai.addCommit(new AuthorCommit((long)rc.getCommitTime(), results[0], results[1]));
				
				bi.incrementCommitCount(1);
				
				prev = rc;
			}
			
			if (prev != null) {
				
				String author = prev.getAuthorIdent().getName();
				AuthorInfo ai = bi.getAuthorInfo(author);
				
				int[] results = compareCommits(prev, null, df);
				
				ai.incrementAdditions(results[0]);
				ai.incrementDeletions(results[1]);
				ai.addCommit(new AuthorCommit((long)prev.getCommitTime(), results[0], results[1]));
				
				bi.incrementCommitCount(1);
					
			}
		}
	}
	
	private void updateRepoInfo(DiffFormatter df) throws IOException {
		
		for (String branch : getBranches()) {
			
			BranchInfo ri = repoInfo.getBranchInfo(branch);
			
			ri.resetInfo();
					
			RevCommit rc = getNewestCommit(branch);
			
			ObjectReader reader = theRepo.getRepository().newObjectReader();
			
			EmptyTreeIterator oldTreeIter = new EmptyTreeIterator();
			oldTreeIter.reset();
			
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			ObjectId newTree = rc.getTree(); 
			newTreeIter.reset(reader, newTree);
	
			df.setRepository(theRepo.getRepository());
			List<DiffEntry> entries = df.scan( oldTreeIter, newTreeIter );
			
			for (DiffEntry entry : entries ) {
				
				Language lang = CodeSniffer.detectLanguage(entry.getNewPath());
				
				ri.incrementLanguage(lang, 1);
				
				ri.incrementFileCount(1);
				
				for (HunkHeader hunk : df.toFileHeader(entry).getHunks()) {
					
					ri.incrementLineCount(hunk.getNewLineCount());
					
				}
				
			}
		}
	}
	
	private int[] compareCommits(RevCommit prev, RevCommit curr, DiffFormatter df) throws IOException {				
		
		ObjectReader reader = theRepo.getRepository().newObjectReader();
	
		AbstractTreeIterator oldTreeIter = null;	
		if (prev == null) {
			
			oldTreeIter = new EmptyTreeIterator();
			((EmptyTreeIterator)oldTreeIter).reset();
			
		} else {
			
			oldTreeIter = new CanonicalTreeParser();
			ObjectId oldTree = prev.getTree(); 
			((CanonicalTreeParser)oldTreeIter).reset(reader, oldTree);
			
		}
		
		AbstractTreeIterator newTreeIter = null;	
		if (curr == null) {
			
			newTreeIter = new EmptyTreeIterator();
			((EmptyTreeIterator)newTreeIter).reset();
			
		} else {
			
			newTreeIter = new CanonicalTreeParser();
			ObjectId newTree = curr.getTree(); 
			((CanonicalTreeParser)newTreeIter).reset(reader, newTree);
			
		}
		
		df.setRepository(theRepo.getRepository());
		List<DiffEntry> entries = df.scan( oldTreeIter, newTreeIter );
		
		int additions = 0;
		
		int deletions = 0;
	
		for (DiffEntry entry : entries) {
			FileHeader fh = df.toFileHeader(entry);
			
			for (HunkHeader hunk : fh.getHunks()){
				
				for (Edit edit : hunk.toEditList()) {
												
					additions += (edit.getEndA() - edit.getBeginA());
					deletions += (edit.getEndB() - edit.getBeginB());
											
				}
			}
		}
		
		return new int[]{additions, deletions};
	}

	private RevCommit getNewestCommit(String branch) {
		
		RevCommit newest = null;
		try {
			
			Iterable<RevCommit> revs = theRepo.log().add(theRepo.getRepository().resolve(branch)).call();
	        
			for (RevCommit rc : revs) {
				
				newest = rc;
				break;
				
			}
			
		} catch (RevisionSyntaxException | IOException | GitAPIException e) {
			LOGGER.error("Could not get most recent commit", e);
		}
		
		return newest;
	}
	
	/**
	 * Gets the general information about this repository:
	 * <ol>
	 * 		<li>Lines of code</li>
	 * 		<li>Number of files</li>
	 * 		<li>Language statistics</li>
	 * </ol>
	 * Currently, this only get information about the master branch.
	 * 
	 * @return a copy of the statistics object. changing this will not effect statistics as a whole.
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
	 * @throws TransportException 
	 * @throws InvalidRemoteException 
	 */
	private void createRepo() throws IllegalStateException, GitAPIException {
			
			theRepo = Git.cloneRepository()
				.setDirectory(theDirectory)
				.setURI(remote)
				.setBare(true)
				.setNoCheckout(true)
				.setCredentialsProvider(cp)
				.call();
			
			sync(false);
		
	}
	
	private File getDirectory(String url) {
		
		UUID name = UUID.nameUUIDFromBytes(url.getBytes());
		return new File(FileUtils.getTempDirectory(), GitRepo.DEFAULT_TEMP_CLONE_DIRECTORY + name.toString());
		
	}
	
	/**
	 * JGit seems to have problems using http to clone, so this attempts to change urls using http
	 * to https instead.
	 * 
	 * <br /><b>Note: </b> This does not attempt to validate the url.
	 * 
	 * @param url
	 * @return the url with https
	 */
	private static String urlScrubber(String url) {
		return url.startsWith("http://") ? url.replace("http://", "https://") : url;
	}
	
	/**
	 * Call this when you are done with this repo.
	 * Only call this when you are done using it, I can't be responsible
	 * for incorrect information if this is used incorrectly. :)
	 */
	public void close() { theRepo.getRepository().close(); }
	
	@Override
	public String toString() {
		return repoInfo.toString();
	}
	
}