package com.cisco.dft.sdk.vcs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;

import com.cisco.dft.sdk.pojo.AuthorCommit;
import com.cisco.dft.sdk.pojo.AuthorInfo;
import com.google.common.collect.Lists;

/**
 * Used to get information about different authors who have committed to a remote repo.
 * 
 * @author phwhitin
 *
 */
public class GitRepo {
	
	/**Default directory relative to the system temp folder to store the repository locally so metrics can be pulled from it*/
	private static final String DEFAULT_TEMP_CLONE_DIRECTORY = "git/";
	
	private Git theRepo;
	
	private final File theDirectory;
	
	private String remote;
	
	private UsernamePasswordCredentialsProvider cp;
	
	private Map<String, AuthorInfo> info = new HashMap<String, AuthorInfo>();
	
	private int loggedCommits = 0;
	
	/**
	 * Links a remote repo with a local version so information can be pulled from it.
	 * When a repo is created, a cached version is stored. This allows for faster
	 * time to get user data for the repository if there is already a local version.
	 * <p>
	 * The repo is cloned bare to only include necessary information. 
	 * 
	 * @param url the url to grab the data from
	 * @param cp authentication needed to access private repos, not necessary for public repos.
	 */
	public GitRepo(String url, UsernamePasswordCredentialsProvider cp) {
		
		remote = urlScrubber(url);
		
		theDirectory = getDirectory(remote);
		
		if (cp != null) this.cp = cp;
		else cp = new UsernamePasswordCredentialsProvider("username", "password");
		
		if (theDirectory.exists()) {
			
			try {
				theRepo = Git.open(theDirectory);
			} catch (IOException e) {
				
				try {
					FileUtils.deleteDirectory(theDirectory);
				} catch (IOException e1) {}
				
				createRepo();
			}
			
		} else {
			
			createRepo();
			
		}
		
	}
	
	/**
	 * Syncs the repository with the remote, updating history if necessary. 
	 * 
	 * @return
	 */
	public List<AuthorInfo> sync() {
		
		DiffFormatter df = new DiffFormatter( new ByteArrayOutputStream() );
		
		try {
			
			try {
				theRepo.fetch().setRemote(this.remote).call();
			} catch (Exception e) {}
			
			Iterable<RevCommit> refs = theRepo.log().all().setSkip(this.loggedCommits).call();
			
			RevCommit prev = null;
			
			for (RevCommit rc : refs) {
				
				++this.loggedCommits;
				String user = rc.getAuthorIdent().getName();
				AuthorInfo ai;
				
				if (!info.containsKey(user)) {
					
					ai = new AuthorInfo(user);
					info.put(user, ai);
					
				} else ai = info.get(user);
				
				ai.incrementCommitCount();
				
				if (prev == null) {
					prev = rc;
					continue;
				}
				
				int[] results = compareCommits(prev, rc, df);
				
				ai.incrementAdditions(results[0]);
				ai.incrementDeletions(results[1]);
				ai.addCommit(new AuthorCommit(Integer.toUnsignedLong(rc.getCommitTime()), results[0], results[1]));
				
				prev = rc;
			}
			
			if (prev != null) {
				
				String user = prev.getAuthorIdent().getName();
				AuthorInfo ai;
				
				if (!info.containsKey(user)) {
					
					ai = new AuthorInfo(user, 0, 0, 0);
					info.put(user, ai);
					
				} else ai = info.get(user);
				
				int[] results = compareCommits(prev, null, df);
				
				ai.incrementAdditions(results[0]);
				ai.incrementDeletions(results[1]);
				ai.addCommit(new AuthorCommit(Integer.toUnsignedLong(prev.getCommitTime()), results[0], results[1]));
				
				
			}
			
			
		} catch (Exception e) {e.printStackTrace();}
				
		df.close();
			
		return getStatistics(false);
		
	}
	
	private int[] compareCommits(RevCommit prev, RevCommit curr, DiffFormatter df) throws IncorrectObjectTypeException, IOException {				
		
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
	
	/**
	 * Gets the statistics that have been logged for the repo.
	 * The data is stored in memory for efficiency, so data may be inaccurate
	 * unless {@link GitRepo#sync()} is run.
	 * 
	 * @return a copy of the statistics for this repo. Author name is used as key 
	 */
	public List<AuthorInfo> getStatistics(boolean sync) {
		
		if (sync) sync();
		
		return Lists.newArrayList(info.values());
		
	}
	
	public List<AuthorInfo> getSortedStatistics(SortMethod method){
		
		List<AuthorInfo> lai = getStatistics(false);
		switch (method) {
			case COMMITS:
				lai.sort((p1, p2) -> Integer.compare(p2.getCommitCount(), p1.getCommitCount()));
				break;
			case ADDITIONS:
				lai.sort((p1, p2) -> Integer.compare(p2.getAdditions(), p1.getAdditions()));
				break;
			case DELETIONS:
				lai.sort((p1, p2) -> Integer.compare(p2.getDeletions(), p1.getDeletions()));
			case NAME:
				lai.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
				break;
			default:
				break;
		}
		
		return lai;
		
	}
	
	/**
	 * Looks up information for a specific user. Don't forget to sync to make sure
	 * that the user information exists.
	 * <p>
	 * <b><u>Note</u></b>: it is possible that the same person have committed
	 * to the repository using different names, so this is not a catch-all.
	 * 
	 * @param user
	 * @return the AuthorInfo for this user if it exists, or null if not.
	 */
	public AuthorInfo lookupUser(String user) {
		return info.containsKey(user) ? info.get(user) : null;
	}
	
	/**
	 * Tries to clone a repo from remote to local.
	 * 
	 * @param theDirectory
	 * @param uri
	 * @param cp
	 */
	private void createRepo() {
		
		try {
			
			theRepo = Git.cloneRepository()
				.setDirectory(theDirectory)
				.setURI(remote)
				.setBare(true)
				.setNoCheckout(true)
				.setCredentialsProvider(cp)
				.call();
			
		} catch (IllegalStateException | GitAPIException e) {
			e.printStackTrace();
		}
		
	}
	
	private File getDirectory(String url) {
		
		UUID name = UUID.nameUUIDFromBytes(url.getBytes());
		
		return new File(FileUtils.getTempDirectory(), GitRepo.DEFAULT_TEMP_CLONE_DIRECTORY + name.toString());
		
	}/**
	 * JGit seems to have problems using http to clone, so this attempts to change urls using http
	 * to https instead.
	 * 
	 * <br /><b>Note: </b> This does not attempt to validate the url.
	 * 
	 * @param url
	 * @return the url with https
	 */
	public static String urlScrubber(String url) {
		return url.startsWith("http://") ? url.replace("http://", "https://") : url;
	}
	
	/**
	 * Test a remote uri to see if it contains a git repository by trying to clone it.
	 * <p>
	 * Returns false if the repository is not a git repository, or valid credentials are
	 * not provided.
	 * 
	 * @param url
	 * @param username username to try to connect with
	 * @param password password to use in trying to establish connection
	 * @return
	 */
	public static boolean doesRemoteRepoExist(String url, String username, String password) {
		
		boolean exists = false;
		
		try {
			
			CredentialsProvider cp = null;
			
			if (!(username == null && password == null)) cp = new UsernamePasswordCredentialsProvider(username, password);
			
			LsRemoteCommand com = Git.lsRemoteRepository()
					.setRemote(urlScrubber(url));
			
			if (cp != null) com.setCredentialsProvider(cp);
			
			com.call();
			
			exists = true;
		} catch (Exception e) {} 
		
		return exists;
	}
	
	/**
	 * Test if a remote uri is a valid git repository.
	 * <p>
	 * Assumes no credentials are needed to access it.
	 * 
	 * @param url
	 * @return
	 */
	public static boolean doesRemoteRepoExist(String url){	

		return doesRemoteRepoExist(url, null, null);
	}
	
	public static enum SortMethod {
		
		COMMITS, ADDITIONS, DELETIONS, NAME
		
	}
	
}
