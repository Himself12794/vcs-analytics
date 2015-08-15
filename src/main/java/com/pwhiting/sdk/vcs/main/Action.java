package com.pwhiting.sdk.vcs.main;

import com.google.common.base.Predicate;

/** Actions the application can perform */
public enum Action {

	ANALYZE("\n  analyze <url> - Gives statistics for a Git repository"
			+ "\n    --branch=<branch> (Limits statistics to specified branch)"
			+ "\n    --nostatistics (Indicates to just clone the repo)"
			+ "\n    --builtin-analysis (Indicates to use builtin stat analysis)"
			+ "\n    --username=<username> (Used for access to private repos)"
			+ "\n    --password=<password> (Used for access to private repos)"
			+ "\n    --start=<epoch-time> (Format: YYYY-MM-DDTHH:MM:SS+HH:MM)"
			+ "\n    --end=<epoch-time> (Format: YYYY-MM-DDTHH:MM:SS+HH:MM)"
			+ "\n    --rev-a=<SVN revision> (SVN only, reads information after this rev)"
			+ "\n    --rev-b=<SVN revision> (SVN only, reads information before this rev)"
			+ "\n    --nocommits (Indicates that only language information should be shown)"
			+ "\n    --svn-source-only (SVN only, skips files that cloc does not consider source code)"
			+ "\n    -s (forces the application to treat the url as a SVN repo)"
			+ "\n    -g (forces the application to treat the url as a Git repo)\n", new Predicate<ProgramConfig>() {

		@Override
		public boolean apply(final ProgramConfig input) {
			return input == null ? false : input.getUrl() != null;
		}
	}), HELP("\n  help <command> - Shows help about the given command"),
	INIT("\n  init  - Extracts CLOC resources"),
	DEBUG("\n  debug - runs the program with some debug data");

	private final String usage;

	private final Predicate<ProgramConfig> validity;

	Action(final String usage) {
		this(usage, new Predicate<ProgramConfig>() {

			@Override
			public boolean apply(final ProgramConfig input) {
				return true;
			}

		});
	}

	Action(final String usage, final Predicate<ProgramConfig> requirements) {
		this.usage = usage;
		validity = requirements;
	}

	public String getUsage() {
		return usage;
	}

	/**
	 * Checks whether or not the command is valid using the given configuration
	 *
	 * @param params
	 * @return
	 */
	public boolean isValid(final ProgramConfig params) {
		return validity.apply(params);
	}
}