package com.cisco.dft.sdk.vcs.svn;

import java.util.Collection;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;

/**
 * Utilities used to perform checks and gather metrics about a remote SVN repository.
 * <p>
 * Authentication is not yet implemented, so metrics can only be gathered about public
 * repositories.
 * 
 * @author phwhitin
 *
 */
public class SVNRepoUtils {
	
	/**
	 * Test a remote uri to see if it contains a svn repository by trying to clone it.
	 * <p>
	 * Returns false if the repository is not a svn repository, or access is forbidden.
	 * 
	 * @param url
	 * @return
	 */
	public static boolean doesRemoteRepoExist(String url) {
		
		boolean exists = false;
		
		SVNURL svnurl;
		try {
			
			svnurl = SVNURL.parseURIEncoded(url);
			
			SVNRepository repo = SVNClientManager.newInstance().createRepository(svnurl, false);
			repo.getLatestRevision();
			
			exists = true;
		} catch (SVNException e) {}
		
		return exists;
	}
	
	/**
	 * Gets commit count for the specified user.
	 * 
	 * @param url the remote repository
	 * @param user the user to check for
	 * @return the amount of commits this user has made, or 0 if the user is not found
	 */
	@SuppressWarnings("rawtypes")
	public static int getCommitCount(String url, String user){
			
		int countedCommits = 0;		
		
		SVNURL svnurl;
		try {
			svnurl = SVNURL.parseURIEncoded(url);
			SVNRepository repo = SVNClientManager.newInstance().createRepository(svnurl, false);
			
			Collection commits = repo.log(null, null, 0, repo.getLatestRevision(), true, false);
			for (Object thing : commits) {
				
				if (thing instanceof SVNLogEntry) {
					SVNLogEntry thing2 = (SVNLogEntry)thing;
					if (thing2.getAuthor().equals(user)) ++countedCommits;
				}
				
			}
		} catch (SVNException e) {}
		
		return countedCommits;
	}
	
}
