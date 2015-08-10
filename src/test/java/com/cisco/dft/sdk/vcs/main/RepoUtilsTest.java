package com.cisco.dft.sdk.vcs.main;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import ch.qos.logback.classic.Level;

import com.cisco.dft.sdk.vcs.core.AuthorCommit;
import com.cisco.dft.sdk.vcs.core.AuthorInfo;
import com.cisco.dft.sdk.vcs.core.AuthorInfoBuilder;
import com.cisco.dft.sdk.vcs.core.BranchInfo;
import com.cisco.dft.sdk.vcs.core.ClocData;
import com.cisco.dft.sdk.vcs.core.ClocData.Header;
import com.cisco.dft.sdk.vcs.core.ClocData.LangStats;
import com.cisco.dft.sdk.vcs.core.GitRepo;
import com.cisco.dft.sdk.vcs.core.HistoryViewer;
import com.cisco.dft.sdk.vcs.core.Repo;
import com.cisco.dft.sdk.vcs.core.util.SortMethod;
import com.cisco.dft.sdk.vcs.util.CodeSniffer;
import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;
import com.cisco.dft.sdk.vcs.util.OSType;
import com.cisco.dft.sdk.vcs.util.Util;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;

public class RepoUtilsTest {

	private static final Logger LOGGER = LoggerFactory.getLogger("UnitTesting");

	@Before
	public void preConfig() {
		Util.setLoggingLevel(Level.DEBUG);
	}

	@Test
	public void testApp() throws GitAPIException, SVNException {

		LOGGER.debug("Testing init feature. (will log errors if system does not allow execute permissions)");
		Application.setConfiguration(ProgramConfig.INIT).execute();

		LOGGER.debug("Testing analyze.");
		Application.setConfiguration(ProgramConfig.TEST).execute();

		LOGGER.debug("Testing debug.");
		Application.setConfiguration(ProgramConfig.DEBUG).execute();

		LOGGER.debug("Testing help feature");
		Application.setConfiguration(ProgramConfig.HELP).execute();

		Application.setConfiguration(ProgramConfig.parseArgs("help", "analyze")).execute();

		LOGGER.debug("Testing force run as SVN");
		Application.setConfiguration(
				ProgramConfig.parseArgs("analyze", "https://github.com/Himself12794/powersAPI.git",
						"-s", "-d")).execute();

	}

	@Test
	public void testAuthorStatGathering() throws Exception {

		final Date start = new Date(1416402821000L);
		final Date end = new Date(1420377221000L);

		LOGGER.info("Testing author information gathering.");
		final GitRepo reo = new GitRepo("https://github.com/pypa/sampleproject.git");
		final AuthorInfoBuilder aib = reo.getRepoStatistics().getBranchInfoFor("master")
				.getAuthorStatistics();
		final AuthorInfo ai = aib.lookupUser("Marcus Smith");
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
		final List<AuthorCommit> commits = ai.getCommits();
		final AuthorCommit ac = ai.getCommitById("f9b9131491db5110f4dbc839f40b94b0f28fcf85");

		assertTrue(ac.getAdditions() == 19);
		assertTrue(ac.getDeletions() == 9);
		assertTrue(ac.getTimestamp().compareTo(commits.get(0).getTimestamp()) >= 0);
		assertTrue(ac.getMessage().equals("Merge pull request #29 from RichardBronosky/master"));
		assertTrue(ac.isMergeCommit());
		assertTrue(ac.getChangedFiles() == 1);
		assertTrue(aib.lookupUser("Unknown").getDeletions() == 0);

		LOGGER.info("Testing date range limiting.");
		assertTrue(aib.getInfo().size() >= 13);
		assertTrue(aib.lookupUser("Matt Iversen").getCommitCount() >= 1);
		System.out.println(aib);

		aib.limitToDateRange(Range.closed(start, end));
		assertTrue(aib.getInfo().size() == 4);
		assertTrue(aib.lookupUser("Matt Iversen").getCommitCount() == 0);
		System.out.println(aib);

		aib.limitToDateRange(Range.open(start, end));
		assertTrue(aib.getInfo().size() == 4);
		assertTrue(aib.lookupUser("Matt Iversen").getCommitCount() == 0);
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
		repo = new GitRepo("https://github.com/pypa/sampleproject.git", new UsernamePasswordCredentialsProvider("username", "password"), "master", false, null);

		repo.sync();
		// System.out.println(repo);
		repo.close();
	}

	@Test
	public void testRepoStatGathering() throws Exception {

		final GitRepo reo = new GitRepo("https://github.com/Himself12794/powersAPI.git", "master", true);

		final BranchInfo branch = reo.getRepoStatistics().getBranchInfoFor("master");

		assertTrue(branch.getLangPercent(Language.JAVA) > 0.0F);
		assertTrue(branch.getLangStats(Language.JAVA).getnFiles() > 0);
		assertTrue(branch.getLangPercent(Language.CSHARP) == 0.0F);
		assertTrue(branch.getLangStats(Language.CSHARP).getnFiles() == 0);
		assertTrue(branch.getBranchName().equals("master"));
		assertTrue(branch.getFileCount() > 10);
		assertTrue(branch.getLineCount() > 200);

		reo.sync("master");

		final Date arbitraryDate = new Date(1434125085000L);

		final HistoryViewer bi = reo.getRepoStatistics().getBranchInfoFor("master");
		final HistoryViewer hv = reo.getRepoStatistics().getBranchInfoFor("master")
				.getHistoryForDate(arbitraryDate);

		System.out.println(bi);
		System.out.println(hv);

		// TODO fix values doubling up again
		assertTrue("8d7abf9d55a6170af465dce7887c4f399d31a7ba".equals(hv.getLastCommitId()));
		LOGGER.info("Uses CLOC statistics: " + String.valueOf(hv.usesCLOCStats()));
		assertTrue(hv.usesCLOCStats() ? hv.getFileCount() == 23 : hv.getFileCount() == 37);
		assertTrue(hv.usesCLOCStats() ? hv.getLineCount() == 2499 : hv.getLineCount() == 5342);
		assertTrue("master".equals(hv.getBranchName()));
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

}
