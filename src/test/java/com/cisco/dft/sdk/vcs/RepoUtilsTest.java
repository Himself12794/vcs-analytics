package com.cisco.dft.sdk.vcs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Test;

import com.cisco.dft.sdk.vcs.repo.AuthorCommit;
import com.cisco.dft.sdk.vcs.repo.AuthorInfo;
import com.cisco.dft.sdk.vcs.repo.AuthorInfoBuilder;
import com.cisco.dft.sdk.vcs.repo.BranchInfo;
import com.cisco.dft.sdk.vcs.repo.GitRepo;
import com.cisco.dft.sdk.vcs.util.CodeSniffer;
import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;
import com.cisco.dft.sdk.vcs.util.SortMethod;

public class RepoUtilsTest {

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
		
		GitRepo repo = new GitRepo("https://github.com/pypa/sampleproject.git");
		repo = new GitRepo("https://github.com/pypa/sampleproject.git", false);
		repo = new GitRepo("https://github.com/pypa/sampleproject.git", new UsernamePasswordCredentialsProvider("username", "password"));
		repo = new GitRepo("https://github.com/pypa/sampleproject.git", new UsernamePasswordCredentialsProvider("username", "password"), false, null);
		
		repo.sync();
		System.out.println(repo);		
	}
	
	@Test 
	public void testRepoStatGathering() throws Exception {
		
		GitRepo reo = new GitRepo("https://github.com/Himself12794/powersAPI.git");
		
		BranchInfo branch = reo.getRepoStatistics().getBranchInfoFor("master");
		
		assertTrue(branch.getLangPercent(Language.JAVA) > 0.0F );
		assertTrue(branch.getLangCount(Language.JAVA) > 0 );
		assertTrue(branch.getLangPercent(Language.C_SHARP) == 0.0F );
		assertTrue(branch.getLangCount(Language.C_SHARP) == 0 );
		assertTrue(branch.getName().equals("master") );
		assertTrue(branch.getFileCount() > 10 );
		assertTrue(branch.getLineCount() > 200 );
		assertTrue(branch.getLangCountMap().containsKey(Language.JAVA) && branch.getLangCountMap().containsKey(Language.OTHER));
		
		reo.sync("master");
	}
	
	@Test 
	public void testAuthorStatGathering() throws Exception {
		
		GitRepo reo = new GitRepo("https://github.com/pypa/sampleproject.git");
		AuthorInfoBuilder aib = reo.getRepoStatistics().getBranchInfoFor("master").getAuthorStatistics();
		AuthorInfo ai = aib.lookupUser("Marcus Smith");
		assertTrue(ai.getCommitCount() >= 26);
		assertFalse(ai.getCommitCount() < 26);
		assertTrue(ai.getAdditions() >= 80);
		assertTrue(ai.getDeletions() >= 86);
		
		aib.sort(SortMethod.ADDITIONS);
		assertTrue(aib.getList().get(0).getAdditions() >= aib.getList().get(1).getAdditions());
		
		aib.sort(SortMethod.COMMITS);
		assertTrue(aib.getList().get(0).getCommitCount() >= aib.getList().get(1).getCommitCount());
		
		aib.sort(SortMethod.DELETIONS);
		assertTrue(aib.getList().get(0).getDeletions() >= aib.getList().get(1).getDeletions());
		
		aib.sort(SortMethod.ADDITIONS);
		assertTrue(aib.getList().get(0).getAdditions() >= aib.getList().get(1).getAdditions());
		
		aib.sort(SortMethod.NAME);
		assertTrue(aib.getList().get(0).getName().compareTo(aib.getList().get(1).getName()) < 0);
		
		List<AuthorCommit> commits = ai.getCommits();
		
		assertTrue(commits.get(0).getTimestamp() > commits.get(1).getTimestamp());
		assertTrue(aib.lookupUser("Unknown").getDeletions() == 0);
		
	}

}
