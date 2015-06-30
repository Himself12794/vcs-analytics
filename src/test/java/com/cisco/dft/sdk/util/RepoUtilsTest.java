package com.cisco.dft.sdk.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.cisco.dft.sdk.vcs.GitRepo;


public class RepoUtilsTest {

	@Test
	public void testURIs() throws Exception {

		assertTrue(GitRepo.doesRemoteRepoExist("https://github.com/twbs/bootstrap.git"));
		assertFalse(GitRepo.doesRemoteRepoExist("http://facebook.com"));
		
		assertTrue(SVNRepoUtils.doesRemoteRepoExist("https://github.com/twbs/bootstrap"));
		assertFalse(SVNRepoUtils.doesRemoteRepoExist("http://facebook.com"));
		
		assertTrue(SVNRepoUtils.getCommitCount("svn://linuxfromscratch.org/BLFS/trunk/BOOK", "fernando") > 0);
	}

}
