package com.pwhiting.sdk.vcs.main;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.wc.SVNRevision;

import ch.qos.logback.classic.Level;

import com.pwhiting.util.ArgMapper;
import com.pwhiting.util.ArgParser;
import com.pwhiting.util.Util;

public class ProgramConfigMapper implements ArgMapper<ProgramConfig> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProgramConfigMapper.class.getSimpleName());
	
	static {
		Util.setLoggingLevel(Level.INFO);
	}

	@Override
	public ProgramConfig mapArguments(ArgParser parser) {

		Action action = parser.getActionAsEnum(Action.class);

		if (action == null) {
			action = Action.HELP;
		}

		final String url = parser.getActionParameterByIndex(0);
		final String branch = parser.getString("branch");
		final String username = parser.getString("username", "");
		final String password = parser.getString("password", "");
		final boolean generateStats = !parser.getBoolean("nostats");
		final boolean generateLangStats = !parser.getBoolean("no-lang-stats");
		final boolean useCloc = !parser.getBoolean("builtin-analysis");
		final boolean version = parser.getBoolean("v");
		final boolean debug = parser.getBoolean("d");
		final boolean noCommits = parser.getBoolean("nocommits");
		final boolean forceGit = parser.getBoolean("forceGit") || parser.getBoolean("g");
		final boolean forceSvn = parser.getBoolean("forceSvn") || parser.getBoolean("s");
		final boolean svnNonSourceSkip = parser.getBoolean("ignore-cache");
		final SVNRevision revA = SVNRevision.create(Util.ifNullDefault(parser.getLong("rev-a"), 0L));
		final SVNRevision revB = parser.getLong("rev-b") != null ? SVNRevision.create(parser.getLong("rev-b")) : SVNRevision.HEAD;
		final Date end = getDate(parser.getString("end"));
		final Date start = getDate(parser.getString("start"));

		final ProgramConfig config = new ProgramConfig(action, url, branch, username, password, generateStats, useCloc);
		config.end = end;
		config.start = start;
		config.debug = debug;
		config.showVersion = version;
		config.noCommits = noCommits;
		config.forceGit = forceGit;
		config.forceSvn = forceSvn;
		config.shouldGenerateLangStats = generateLangStats;
		config.svnIgnoreCache = svnNonSourceSkip;
		config.revA = revA;
		config.revB = revB;
		
		return config;
	}

	private Date getDate(final String date) {

		try {
			return DateTime.parse(date).toDateTime(DateTimeZone.getDefault()).toDate();
		} catch (final Exception iae) {
			LOGGER.trace("Could not read start input as a DateTime object", iae);
		}

		try {
			return new Date(Long.parseLong(date));
		} catch (final NumberFormatException nfe) {
			// No action needed
		}

		return null;

	}

}
