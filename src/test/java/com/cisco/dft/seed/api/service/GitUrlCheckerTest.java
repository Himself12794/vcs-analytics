package com.cisco.dft.seed.api.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.cisco.dft.seed.api.util.GitRepoUtils;


public class GitUrlCheckerTest {

	@Test
	public void testURIs() throws Exception {

		assertEquals(GitRepoUtils.testRemoteRepo("https://github.com/twbs/bootstrap.git"), true);
		assertEquals(GitRepoUtils.testRemoteRepo("http://facebook.com"), false);
	}

}
