package com.cisco.dft.sdk.vcs.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

import com.cisco.dft.sdk.vcs.core.AuthorInfoBuilder;
import com.cisco.dft.sdk.vcs.core.BranchInfo;
import com.cisco.dft.sdk.vcs.core.ClocData;
import com.cisco.dft.sdk.vcs.core.ClocData.Header;
import com.cisco.dft.sdk.vcs.core.ClocData.LangStats;
import com.cisco.dft.sdk.vcs.core.Commit;
import com.cisco.dft.sdk.vcs.core.CommitterInfo;
import com.cisco.dft.sdk.vcs.core.GitRepo;
import com.cisco.dft.sdk.vcs.core.HistoryViewer;
import com.cisco.dft.sdk.vcs.core.Repo;
import com.cisco.dft.sdk.vcs.core.SVNRepo;
import com.cisco.dft.sdk.vcs.core.error.CommitterNotFoundException;
import com.cisco.dft.sdk.vcs.core.util.SortMethod;
import com.cisco.dft.sdk.vcs.util.CodeSniffer;
import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;
import com.cisco.dft.sdk.vcs.util.OSType;
import com.cisco.dft.sdk.vcs.util.Util;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;

public class RepoUtilsTest {

	private static final Logger LOGGER = LoggerFactory.getLogger("UnitTesting");
	
	private static final String DEVELOP = "develop";
	
	private static final String MASTER = "master";

	@Before
	public void preConfig() {
		Util.setLoggingLevel(Level.DEBUG);
	}

	@Test
	public void testApp() throws Exception {

		LOGGER.debug("Testing init feature. (will log errors if system does not allow execute permissions)");
		Application.setConfiguration(ProgramConfig.INIT).execute();

		LOGGER.debug("Testing analyze.");
		Application.setConfiguration(ProgramConfig.TEST).execute();

		LOGGER.debug("Testing help feature");
		Application.setConfiguration(ProgramConfig.HELP).execute();
		
		LOGGER.debug("Testing help with option");
		Application.setConfiguration(ProgramConfig.parseArgs("help", "analyze")).execute();

		LOGGER.debug("Testing force run as SVN");
		Application.setConfiguration(
				ProgramConfig.parseArgs("analyze", "https://github.com/Himself12794/powersAPI.git",
						"-s", "-d", "--nostats")).execute();

	}

	@Test
	public void testAuthorStatGathering() throws Exception {

		final Date start = new Date(1416402821000L);
		final Date end = new Date(1420377221000L);

		LOGGER.info("Testing author information gathering.");
		final GitRepo reo = new GitRepo("https://github.com/pypa/sampleproject.git");
		final AuthorInfoBuilder aib = reo.getRepoStatistics().getBranchInfoFor(MASTER)
				.getAuthorStatistics();
		final CommitterInfo ai = aib.lookupUser("Marcus Smith");
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
		assertTrue(aib.getInfo().get(0).getCommitterName().compareTo(aib.getInfo().get(1).getCommitterName()) < 0);

		LOGGER.info("Testing commit info accuracy");
		final List<Commit> commits = ai.getCommits();
		final Commit ac = ai.getCommitById("f9b9131491db5110f4dbc839f40b94b0f28fcf85");

		assertTrue(ac.getAdditions() == 19);
		assertTrue(ac.getDeletions() == 9);
		assertTrue(ac.getTimestamp().compareTo(commits.get(0).getTimestamp()) >= 0);
		assertTrue(ac.getMessage().equals("Merge pull request #29 from RichardBronosky/master"));
		assertTrue(ac.isMergeCommit());
		assertTrue(ac.getChangedFiles() == 1);
		
		boolean success = false;
		
		try {
			aib.lookupUser("Unknown");
			success = false;
		} catch (Exception e) {
			success = true;
		} finally {
			assertTrue(success);
		}

		LOGGER.info("Testing date range limiting.");
		assertTrue(aib.getInfo().size() >= 13);
		assertTrue(aib.lookupUser("Matt Iversen").getCommitCount() >= 1);
		System.out.println(aib);

		aib.limitToDateRange(Range.closed(start, end));
		assertTrue(aib.getInfo().size() == 4);
		try {
			assertTrue(aib.lookupUser("Matt Iversen").getCommitCount() == 0);
			assertTrue(false);
		} catch (CommitterNotFoundException cnfe) {
			assertTrue(true);
		}
		System.out.println(aib);

		reo.close();

	}

	@Test
	public void testCodeSniffer() throws Exception {

		assertTrue(CodeSniffer.detectLanguage(new File("Test.java")) == Language.JAVA);
		assertTrue(CodeSniffer.detectLanguage(new File("Test")) == Language.UNDEFINED);
		assertTrue(CodeSniffer.detectLanguage("Test.java") == Language.JAVA);
		assertTrue(CodeSniffer.detectLanguage("Test.none") == Language.UNDEFINED);
		assertTrue(CodeSniffer.detectLanguage("") == Language.UNDEFINED);
		assertEquals("Should have been a pom.xml", CodeSniffer.detectLanguage("pom.xml"), Language.MAVEN);
		LOGGER.debug("OS Type: " + OSType.getOSType().name());

	}

	@Test
	public void testConfig() {

		LOGGER.debug("Testing configuration");
		assertTrue(ProgramConfig.DEBUG.shouldShowCommits());
		assertTrue(ProgramConfig.DEBUG.getStart() == null);
		assertTrue(ProgramConfig.DEBUG.getEnd() == null);
		assertTrue(ProgramConfig.TEST.getStart() != null);
		assertTrue(ProgramConfig.TEST.getEnd() != null);

	}

	@Test
	public void testRepoInitialization() throws Exception {

		GitRepo repo = new GitRepo("https://github.com/pypa/sampleproject.git");
		repo = new GitRepo("https://github.com/pypa/sampleproject.git", false);
		repo = new GitRepo("https://github.com/pypa/sampleproject.git", new UsernamePasswordCredentialsProvider("username", "password"));
		repo = new GitRepo("https://github.com/pypa/sampleproject.git", new UsernamePasswordCredentialsProvider("username", "password"), MASTER, false, null);

		repo.sync();
		// System.out.println(repo);
		repo.close();
	}

	@Test
	public void testRepoStatGathering() throws Exception {

		final GitRepo reo = new GitRepo("https://github.com/Himself12794/powersAPI.git", MASTER, true);

		final BranchInfo branch = reo.getRepoStatistics().getBranchInfoFor(MASTER);

		assertTrue(branch.getLangPercent(Language.JAVA) > 0.0F);
		assertTrue(branch.getLangStats(Language.JAVA).getnFiles() > 0);
		assertTrue(branch.getLangPercent(Language.CSHARP) == 0.0F);
		assertTrue(branch.getLangStats(Language.CSHARP).getnFiles() == 0);
		assertTrue(branch.getBranchName().equals(MASTER));
		assertTrue(branch.getFileCount() > 10);
		assertTrue(branch.getLineCount() > 200);

		reo.sync(MASTER);

		final Date arbitraryDate = new Date(1434125085000L);

		final HistoryViewer bi = reo.getRepoStatistics().getBranchInfoFor(MASTER);
		final HistoryViewer hv = reo.getRepoStatistics().getBranchInfoFor(MASTER)
				.getHistoryForDate(arbitraryDate);

		System.out.println(bi);
		System.out.println(hv);

		// TODO fix values doubling up again
		assertTrue("8d7abf9d55a6170af465dce7887c4f399d31a7ba".equals(hv.getLastCommitId()));
		LOGGER.info("Uses CLOC statistics: " + String.valueOf(hv.usesCLOCStats()));
		assertTrue(hv.usesCLOCStats() ? hv.getFileCount() == 23 : hv.getFileCount() == 37);
		assertTrue(hv.usesCLOCStats() ? hv.getLineCount() == 2499 : hv.getLineCount() == 5342);
		assertTrue(MASTER.equals(hv.getBranchName()));
		assertTrue(hv.usesCLOCStats() ? hv.getLangStats(Language.JAVA).getnFiles() == 23 : hv
				.getLangStats(Language.JAVA).getnFiles() == 23);
		assertTrue(hv.getLangPercent(Language.JAVA) >= 0.5F);
		assertTrue(hv.getDate().equals(arbitraryDate));
		
		LOGGER.info("Testing code sniffer over directory");
		System.out.println(CodeSniffer.analyzeDirectory(reo.getDirectory()));
		
		reo.close();
	}

	@Test
	public void testUtility() {

		final String test1 = "test1";
		final String test2 = "test2";
		final String test3 = "my-project";

		final Map<String, Boolean> map = Maps.newHashMap();

		map.put(test1, true);

		assertTrue(Util.getOrDefault(map, test1, false));
		assertTrue(Util.getOrDefault(map, test2, true));
		assertTrue(Util.putIfAbsent(map, test1, false));
		assertTrue(Util.putIfAbsent(map, test2, true));

		final Map<Language, LangStats> map2 = Maps.newHashMap();

		final ClocData data = new ClocData(new Header(), map2);

		LOGGER.debug(data.toString());

		test3.equals(Repo.guessName("http://my-project.git"));
		test3.equals(Repo.guessName("http://my-project"));
		test3.equals(Repo.guessName("http:\\my-project"));

	}
	
	@Test
	public void testSVN() throws Exception {
		
		SVNRepo repo = new SVNRepo("https://github.com/Himself12794/Heroes-Mod", "branches/" + DEVELOP);
		repo.sync("branches/bugfix/fix-broken-things", true, true);
		
		System.out.println(repo);
		
		assertTrue(repo.getRepoStatistics().branchExists("branches/" + DEVELOP));
		assertEquals("Wrong commit count", 6, repo.getRepoStatistics().getBranchInfoFor("branches/" + DEVELOP).getCommitCount());
		
	}

}
