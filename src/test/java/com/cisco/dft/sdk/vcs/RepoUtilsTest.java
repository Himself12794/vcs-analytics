package com.cisco.dft.sdk.vcs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Test;

import com.cisco.dft.sdk.vcs.common.AuthorCommit;
import com.cisco.dft.sdk.vcs.common.AuthorInfo;
import com.cisco.dft.sdk.vcs.common.AuthorInfoViewBuilder;
import com.cisco.dft.sdk.vcs.common.BranchInfo;
import com.cisco.dft.sdk.vcs.git.GitRepo;
import com.cisco.dft.sdk.vcs.util.CodeSniffer;
import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;
import com.cisco.dft.sdk.vcs.util.SortMethod;


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
		assertTrue(CodeSniffer.detectLanguage("") == Language.OTHER);
	
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
		
		BranchInfo branch = reo.getRepoStatistics().lookupBranch("refs/heads/master");
		
		assertTrue(branch.getLangPercent(Language.JAVA) > 0.0F );
		assertTrue(branch.getLangCount(Language.JAVA) > 0 );
		assertTrue(branch.getLangPercent(Language.C_SHARP) == 0.0F );
		assertTrue(branch.getLangCount(Language.C_SHARP) == 0 );
		assertTrue(branch.getBranch().equals("refs/heads/master") );
		assertTrue(branch.getFileCount() > 10 );
		assertTrue(branch.getLineCount() > 200 );
		assertTrue(branch.getLangCountMap().containsKey(Language.JAVA) &&  branch.getLangCountMap().containsKey(Language.OTHER));
		
		// This is just to satisfy sonar
		BranchInfo ri = new BranchInfo();
		
		ri.incrementFileCount(1);
		ri.incrementLanguage(Language.HTML, 1);
		ri.incrementLineCount(1);
		
		assertTrue( ri.getFileCount() == 1);
		assertTrue( ri.getLangCount(Language.HTML) == 1);
		assertTrue( ri.getLineCount() == 1);
		
	}
	
	@Test 
	public void testAuthorStatGathering() throws Exception {
		
		GitRepo reo = new GitRepo("https://github.com/pypa/sampleproject.git");
		AuthorInfo ai = reo.getAuthorStatistics().lookupUser("Marcus Smith");
		assertTrue(ai.getCommitCount() >= 26);
		assertTrue(ai.getCommitCount() >= 26);
		assertFalse(ai.getCommitCount() > 26);
		assertTrue(ai.getAdditions() >= 106);
		assertTrue(ai.getDeletions() >= 86);
		
		AuthorInfoViewBuilder aivb = reo.getAuthorStatistics().sort(SortMethod.ADDITIONS);
		assertTrue(aivb.getList().get(0).getAdditions() >= aivb.getList().get(1).getAdditions());
		
		aivb.sort(SortMethod.COMMITS);
		assertTrue(aivb.getList().get(0).getCommitCount() >= aivb.getList().get(1).getCommitCount());
		
		aivb.sort(SortMethod.DELETIONS);
		assertTrue(aivb.getList().get(0).getDeletions() >= aivb.getList().get(1).getDeletions());
		
		aivb.sort(SortMethod.ADDITIONS);
		assertTrue(aivb.getList().get(0).getAdditions() >= aivb.getList().get(1).getAdditions());
		
		aivb.sort(SortMethod.NAME);
		assertTrue(aivb.getList().get(0).getName().compareTo(aivb.getList().get(1).getName()) < 0);
		
		List<AuthorCommit> commits = ai.getCommits();
		
		assertTrue(commits.get(0).getAdditions() > 5);
		assertTrue(commits.get(0).getDeletions() > 5);
		assertTrue(commits.get(0).getTimestamp() > commits.get(1).getTimestamp());
		assertTrue(reo.getAuthorStatistics().lookupUser("Unknown").getDeletions() == 0);
		
	}

}
