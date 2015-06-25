package com.cisco.dft.seed.api.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 * Utilities used to perform checks and gather metrics about a remote Git repository.
 * 
 * @author phwhitin
 *
 */
public class GitRepoUtils {
	
	/**Default directory to store the repository locally so metrics can be pulled from it*/
	private static final File DEFAULT_TEMP_CLONE_DIRECTORY = new File("temp/git/test");
	
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
	public static boolean testRemoteRepo(String url, String username, String password) {
		
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
	public static boolean testRemoteRepo(String url){
		
		return testRemoteRepo(url, null, null);
		
	}
	
	/**
	 * Gets commit count for the specified user.
	 * <p>
	 * The only way to check this is to clone a local copy, and use git log,
	 * so systems that do not allow read/write permissions will not allow this 
	 * action.
	 * 
	 * @param url the remote repository
	 * @param username for validation
	 * @param password for validation
	 * @param user the user to check for
	 * @return the amount of commits this user has made, or 0 if the user is not found
	 */
	public static int getCommitCount(String url, String username, String password, String user){
			
		int countedCommits = 0;
		
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
						 
			Iterable<RevCommit> refs = com.call().log().call();
			 
			for (RevCommit rc : refs) {
				
				if (rc.getAuthorIdent().getName().equals(user)) countedCommits++;
			}
			
		} catch (Exception e) {
		} finally {
			
			try {
				FileUtils.deleteDirectory(DEFAULT_TEMP_CLONE_DIRECTORY);
			} catch (IOException e) {}
			
		}	
		
		return countedCommits;
		
	}

	public static int getCommitCount(String url, String user){
		return getCommitCount(url, null, null, user);
	}
	
}
