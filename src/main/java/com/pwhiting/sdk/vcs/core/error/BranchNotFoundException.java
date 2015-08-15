package com.pwhiting.sdk.vcs.core.error;

import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Thrown when a branch is tried to be accessed when it doesn't exist.
 * 
 * @author phwhitin
 *
 */
public class BranchNotFoundException extends GitAPIException {

	private static final long serialVersionUID = 7093767439732861675L;

	public BranchNotFoundException(final String message) {
		super(message);
	}

}
