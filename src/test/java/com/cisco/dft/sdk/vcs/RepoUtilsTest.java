package com.cisco.dft.sdk.vcs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Test;

import com.cisco.dft.sdk.vcs.git.GitRepo;
import com.cisco.dft.sdk.vcs.util.CodeSniffer;
import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;


public class RepoUtilsTest {

	@Test
	public void testURIs() throws Exception {
		
		assertTrue(GitRepo.doesRemoteRepoExist("https://github.com/twbs/bootstrap.git", "username", "password"));
		assertFalse(GitRepo.doesRemoteRepoExist("http://facebook.com"));
	
	}

	@Test
	public void testCodeSniffer() throws Exception {
		
		assertTrue(CodeSniffer.detectLanguage(new File("Test.java")) == Language.JAVA);
		assertTrue(CodeSniffer.detectLanguage(new File("Test")) == Language.OTHER);
		assertTrue(CodeSniffer.detectLanguage("Test.java") == Language.JAVA);
		assertTrue(CodeSniffer.detectLanguage("Test.none") == Language.OTHER);
	
	}
	
	@Test
	public void testRepoInitialization() throws Exception {
		
		GitRepo reo = new GitRepo("https://github.com/pypa/sampleproject.git");
		reo = new GitRepo("https://github.com/pypa/sampleproject.git", false);
		reo = new GitRepo("https://github.com/pypa/sampleproject.git", new UsernamePasswordCredentialsProvider("username", "password"));
		reo = new GitRepo("https://github.com/pypa/sampleproject.git", new UsernamePasswordCredentialsProvider("username", "password"), false);
		
		reo.sync();
		System.out.println(reo);		
	}
	
	@Test 
	public void testRepoStatGathering() throws Exception {
		
		GitRepo reo = new GitRepo("https://github.com/Himself12794/powersAPI.git");
		assertTrue(reo.getRepoStatistics().getLangCount(Language.JAVA) > 0 );
		assertTrue(reo.getRepoStatistics().getFileCount() > 10 );
		assertTrue(reo.getRepoStatistics().getLineCount() > 200 );
		assertTrue(reo.getRepoStatistics().getLangCountMap().containsKey(Language.JAVA) &&  reo.getRepoStatistics().getLangCountMap().containsKey(Language.OTHER));
		
	}
	
	@Test 
	public void testAuthorStatGathering() throws Exception {
		
		GitRepo reo = new GitRepo("https://github.com/pypa/sampleproject.git");
		assertTrue(reo.getAuthorStatistics().lookupUser("Marcus Smith").getCommitCount() >= 26);
		assertFalse(reo.getAuthorStatistics().lookupUser("Marcus Smith").getCommitCount() > 26);
		assertTrue(reo.getAuthorStatistics().lookupUser("Marcus Smith").getAdditions() >= 106);
		assertTrue(reo.getAuthorStatistics().lookupUser("Marcus Smith").getDeletions() >= 86);
		assertTrue(reo.getAuthorStatistics().lookupUser("Unknown").getDeletions() == 0);
		
	}

}
