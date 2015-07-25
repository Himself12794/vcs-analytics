package com.cisco.dft.sdk.vcs.common;

import org.eclipse.jgit.api.errors.GitAPIException;

public class BranchNotFoundException extends GitAPIException {

	private static final long serialVersionUID = 7093767439732861675L;

	public BranchNotFoundException(String message) {
		super(message);
	}

}
