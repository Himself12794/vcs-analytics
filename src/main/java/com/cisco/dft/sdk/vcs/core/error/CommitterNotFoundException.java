package com.cisco.dft.sdk.vcs.core.error;

import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Thrown when a user is searched for that doesn't exist.
 * 
 * @author phwhitin
 *
 */
public class CommitterNotFoundException extends GitAPIException {

	private static final long serialVersionUID = 1L;
	
	public CommitterNotFoundException(String message) {
		super(message);
	}

	public CommitterNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
