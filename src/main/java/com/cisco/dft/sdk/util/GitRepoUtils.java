package com.cisco.dft.sdk.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

/**
 * Utilities used to perform checks and gather metrics about a remote Git repository.
 * <p>
 * <b><u>Note</u></b>: for large repositories, operations other than url verification
 * might take several minutes to process all the author and commit information. If the git repo
 * is located at GitHub, use the Subversion clone link with {@link SVNRepoUtils} instead 
 * since SVN does not require a local copy to pull metrics.
 * 
 * @author phwhitin
 *
 */
public class GitRepoUtils {
	
	/**Default directory to store the repository locally so metrics can be pulled from it*/
	private static final File DEFAULT_TEMP_CLONE_DIRECTORY = new File("temp/git");
	
	/**
	 * JGit can only perform requests using https, so this attempts to change urls using http
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
	
	/**
	 * <u>Don't use this yet, it'll only cause heartbreak. It works, but it could work better. It needs
	 * to be refined before getting the stamp of approval.</u>
	 * <p>
	 * Gets commit count for the specified user.
	 * <p>
	 * The only way to check this is to clone a local copy, and use git log,
	 * so systems that do not allow read/write permissions will not allow this 
	 * action.
	 * <p>
	 * Due to the that commit information is stored, the values will be off if the user was also the one
	 * who made the initial commit.
	 * 
	 * @param url the remote repository
	 * @param username for validation
	 * @param password for validation
	 * @param user the user to check for
	 * @return an AuthorInfo object containing the information
	 */
	@Deprecated
	public static AuthorInfo getCommitCount(String url, String username, String password, String user){
			
		int countedCommits = 0;
		
		int totalAdditions = 0;
		
		int totalDeletions = 0;
		
		CredentialsProvider cp = null;
		
		if (!(username == null && password == null)) cp = new UsernamePasswordCredentialsProvider(username, password);
		
		try {
			
			try {
				FileUtils.deleteDirectory(DEFAULT_TEMP_CLONE_DIRECTORY);
			} catch (IOException e) {
			}
			
			 CloneCommand com = Git.cloneRepository().setURI(urlScrubber(url))
				.setDirectory(DEFAULT_TEMP_CLONE_DIRECTORY);
			
			if (cp != null) com.setCredentialsProvider(cp);
			
			Git repoGit = com.call();
			
			Iterable<RevCommit> refs = repoGit.log().call();
			 
			RevCommit prev = null;
			
			for (RevCommit rc : refs) {
				
				if (rc.getAuthorIdent().getName().equals(user)) countedCommits++;
				
				if (prev == null) {
					
					prev = rc;
					continue;
					
				}
				
				ObjectReader reader = repoGit.getRepository().newObjectReader();
				
				CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
				ObjectId oldTree = prev.getTree(); 
				oldTreeIter.reset( reader, oldTree );
				
				CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
				ObjectId newTree = rc.getTree(); 
				newTreeIter.reset( reader, newTree );
				
				DiffFormatter df = new DiffFormatter( new ByteArrayOutputStream() );
				df.setRepository( repoGit.getRepository() );
				List<DiffEntry> entries = df.scan( oldTreeIter, newTreeIter );

				for ( DiffEntry entry : entries ) {
					FileHeader fh = df.toFileHeader(entry);						
					
					for (HunkHeader hunk : fh.getHunks()){
						
						for (Edit edit : hunk.toEditList()) {
							
							if (rc.getAuthorIdent().getName().equals(user)) {
								
								final int additions = (edit.getEndA() - edit.getBeginA());
								final int deletions = (edit.getEndB() - edit.getBeginB());
																
								totalAdditions += additions;
								totalDeletions += deletions;
								
							}
						}
						
					}
				}
				
				df.close();
				
				prev = rc;
			}
			
		} catch (Exception e) {e.printStackTrace();
		} finally {
			
			try {
				FileUtils.deleteDirectory(DEFAULT_TEMP_CLONE_DIRECTORY);
			} catch (IOException e) {}
			
		}	
		
		if (countedCommits > 0) {
		
			return new AuthorInfo(user, countedCommits, totalAdditions, totalDeletions);
			
		}
			
		return null;
		
	}
	
	/**
	 * <u>Don't use this yet, it'll only cause heartbreak. It works, but it could work better. It needs
	 * to be refined before getting the stamp of approval.</u>
	 * <p>
	 * Gets commit count for the specified user.
	 * <p>
	 * The only way to check this is to clone a local copy, and use git log,
	 * so systems that do not allow read/write permissions will not allow this 
	 * action.
	 * <p>
	 * Leaving out the password and username assumes that the repository is public.
	 * 
	 * @param url the remote repository
	 * @param user the user to check for
	 * @return the amount of commits this user has made, or 0 if the user is not found
	 */
	@Deprecated
	public static AuthorInfo getCommitCount(String url, String user){
		return getCommitCount(url, null, null, user);
	}
	
	/**
	 * Wrapper class used for author data pulled from the repository.
	 * 
	 * @author phwhitin
	 *
	 */
	public static class AuthorInfo {
		
		private final String name;
		
		private final int commits;
		
		private final int additions;
		
		private final int deletions;
		
		private AuthorInfo(final String name, final int commits, final int additions, final int deletions) {
			
			this.name = name;
			this.commits = commits;
			this.additions = additions;
			this.deletions = deletions;
			
		}
		
		public String getName() {return this.name;}
		
		public int getCommits() {return this.commits;}
		
		public int getAdditions() {return this.additions;}
		
		public int getDeletions() {return this.deletions;}
		
		@Override
		public String toString() {
			String value = "";
			value += "Name: " + name + ", ";
			value += "Commits: " + commits + ", ";
			value += "Additions: " + additions + ", ";
			value += "Deletions: " + deletions;
			
			return value;
		}
		
	}
	
}
