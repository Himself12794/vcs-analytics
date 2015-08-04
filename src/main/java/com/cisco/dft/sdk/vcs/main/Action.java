package com.cisco.dft.sdk.vcs.main;

import com.google.common.base.Predicate;

/** Actions the application can perform */
public enum Action {

	ANALYZE("Gets statistics for a remote repo. Usage: analyze <url>\n", new Predicate<ProgramConfig>() {

		@Override
		public boolean apply(ProgramConfig input) {
			return input == null ? false : input.getUrl() != null;
		}

	}), HELP("Shows usage for an action."), INIT("Does a test initialization of cloc"), DEBUG("Runs with preset debug params");

	private final String usage;

	private final Predicate<ProgramConfig> validity;

	Action(String usage) {
		this(usage, new Predicate<ProgramConfig>() {

			@Override
			public boolean apply(ProgramConfig input) {
				return true;
			}

		});
	}

	Action(String usage, Predicate<ProgramConfig> requirements) {
		this.usage = usage;
		this.validity = requirements;
	}

	public String getUsage() {
		return usage;
	}

	/**
	 * Checks whether or not the command is valid using the given
	 * configuration
	 * 
	 * @param params
	 * @return
	 */
	public boolean isValid(ProgramConfig params) {
		return validity.apply(params);
	}
}