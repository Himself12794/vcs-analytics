package com.cisco.dft.seed.api.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.cisco.dft.seed.api.util.GitRepoUtils;
import com.cisco.dft.seed.api.util.SVNRepoUtils;


public class RepoUtilsTest {

	@Test
	public void testURIs() throws Exception {

		assertTrue(GitRepoUtils.doesRemoteRepoExist("https://github.com/twbs/bootstrap.git"));
		assertFalse(GitRepoUtils.doesRemoteRepoExist("http://facebook.com"));
		
		assertTrue(SVNRepoUtils.doesRemoteRepoExist("https://github.com/twbs/bootstrap"));
		assertFalse(SVNRepoUtils.doesRemoteRepoExist("http://facebook.com"));
		
		assertTrue(SVNRepoUtils.getCommitCount("svn://linuxfromscratch.org/BLFS/trunk/BOOK", "fernando") > 0);
	}

}
