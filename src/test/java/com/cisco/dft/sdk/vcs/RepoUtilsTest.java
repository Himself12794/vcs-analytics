package com.cisco.dft.sdk.vcs;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.dft.sdk.vcs.repo.AuthorCommit;
import com.cisco.dft.sdk.vcs.repo.AuthorInfo;
import com.cisco.dft.sdk.vcs.repo.AuthorInfoBuilder;
import com.cisco.dft.sdk.vcs.repo.BranchInfo;
import com.cisco.dft.sdk.vcs.repo.GitRepo;
import com.cisco.dft.sdk.vcs.util.CodeSniffer;
import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;
import com.cisco.dft.sdk.vcs.util.SortMethod;

public class RepoUtilsTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("UnitTesting");

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
		repo.close();
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
		
		reo.close();
	}
	
	@Test 
	public void testAuthorStatGathering() throws Exception {
		
		Date start = new Date(1416402821000L);
		Date end = new Date(1420377221000L);
		
		LOGGER.info("Testing author information gathering.");
		GitRepo reo = new GitRepo("https://github.com/pypa/sampleproject.git");
		AuthorInfoBuilder aib = reo.getRepoStatistics().getBranchInfoFor("master").getAuthorStatistics();
		AuthorInfo ai = aib.lookupUser("Marcus Smith");
		assertTrue(ai.getCommitCount() >= 26);
		assertTrue(ai.getAdditions() >= 106);
		assertTrue(ai.getDeletions() >= 86);
		
		LOGGER.info("Testing author statistics sorting methods.");
		aib.sort(SortMethod.ADDITIONS);
		assertTrue(aib.getInfo().get(0).getAdditions() >= aib.getInfo().get(1).getAdditions());
		
		aib.sort(SortMethod.COMMITS);
		assertTrue(aib.getInfo().get(0).getCommitCount() >= aib.getInfo().get(1).getCommitCount());
		
		aib.sort(SortMethod.DELETIONS);
		assertTrue(aib.getInfo().get(0).getDeletions() >= aib.getInfo().get(1).getDeletions());
		
		aib.sort(SortMethod.NAME);
		assertTrue(aib.getInfo().get(0).getName().compareTo(aib.getInfo().get(1).getName()) < 0);
		
		LOGGER.info("Testing commit info accuracy");
		List<AuthorCommit> commits = ai.getCommits();
		AuthorCommit ac = ai.getCommitById("f9b9131491db5110f4dbc839f40b94b0f28fcf85");
		
		assertTrue(ac.getAdditions() == 19);
		assertTrue(ac.getDeletions() == 9);
		assertTrue(ac.getTimestamp() >= commits.get(0).getTimestamp());
		assertTrue(ac.getMessage().equals("Merge pull request #29 from RichardBronosky/master"));
		assertTrue(ac.isMergeCommit());
		assertTrue(ac.getChangedFiles() == 1);
		assertTrue(ac.getTotalChange() == 10);
		assertTrue(aib.lookupUser("Unknown").getDeletions() == 0);
		
		LOGGER.info("Testing date range limiting.");
		assertTrue(aib.getInfo().size() >= 13);
		assertTrue(aib.lookupUser("Matt Iversen").getCommitCount() >= 1 );
		System.out.println(aib);
		aib.limitToDateRange(start, end, true);
		assertTrue(aib.getInfo().size() == 4);
		assertTrue(aib.lookupUser("Matt Iversen").getCommitCount() == 0 );
		System.out.println(aib);
		
		reo.close();
		
	}

}
;