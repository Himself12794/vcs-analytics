package com.cisco.dft.sdk.vcs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.cisco.dft.sdk.vcs.common.RepoInfo;
import com.cisco.dft.sdk.vcs.git.GitRepo;
import com.cisco.dft.sdk.vcs.svn.SVNRepoUtils;
import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;


public class RepoUtilsTest {

	@Test
	public void testURIs() throws Exception {
		
		assertTrue(GitRepo.doesRemoteRepoExist("https://github.com/twbs/bootstrap.git"));
		assertFalse(GitRepo.doesRemoteRepoExist("http://facebook.com"));
		
		assertTrue(SVNRepoUtils.doesRemoteRepoExist("https://github.com/twbs/bootstrap"));
		assertFalse(SVNRepoUtils.doesRemoteRepoExist("http://facebook.com"));
		
		assertTrue(SVNRepoUtils.getCommitCount("svn://linuxfromscratch.org/BLFS/trunk/BOOK", "fernando") > 0);
	
	}
	
	@Test 
	public void testRepoStatGathering() throws Exception {
		
		GitRepo reo = new GitRepo("https://github.com/pypa/sampleproject.git");
		RepoInfo ri = reo.getRepoStatistics();
		assertTrue(ri.getLangPercent(Language.PYTHON) > 0.0F);
		
		/*reo = new GitRepo("https://github.com/twbs/bootstrap.git");
		ri = reo.getRepoStatistics();
		assertTrue(ri.getLangPercent(Language.HTML) > ri.getLangPercent(Language.JAVASCRIPT));
		assertTrue(ri.getLangCount(Language.PYTHON) == ri.getLangCount(Language.XML));
		assertTrue(ri.getFileCount() > 300);
		assertTrue(ri.getLineCount() > 90000);*/
		
	}
	
	@Test 
	public void testAuthorStatGathering() throws Exception {
		
		GitRepo reo = new GitRepo("https://github.com/pypa/sampleproject.git");
		assertTrue(reo.getAuthorStatistics().lookupUser("Marcus Smith").getCommitCount() >= 26);
		assertFalse(reo.getAuthorStatistics().lookupUser("Marcus Smith").getCommitCount() > 26);
		assertTrue(reo.getAuthorStatistics().lookupUser("Marcus Smith").getAdditions() >= 106);
		assertTrue(reo.getAuthorStatistics().lookupUser("Marcus Smith").getDeletions() >= 86);
		

		reo = new GitRepo("https://github.com/Himself12794/powersAPI.git");
		assertTrue(reo.getAuthorStatistics().lookupUser("Philip Whiting").getCommitCount() == 3);
		assertTrue(reo.getRepoStatistics().getLangCount(Language.JAVA) == 50 );
		
	}

}
