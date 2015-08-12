package com.cisco.dft.sdk.vcs.core.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.dft.sdk.vcs.core.Commit;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

/**
 * Tool for storing commits to log.
 * 
 * @author phwhitin
 *
 */
public class CommitLogger {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CommitLogger.class.getSimpleName());

	private static final String LOG_FILE_PATH = "logs/";
	
	private static final String LOG_FILE = "commit-log.json";
	
	private final File logFile;
	
	private boolean isInit;
	
	public CommitLogger(File file) {
		logFile = new File(file, LOG_FILE_PATH + LOG_FILE);
		try {
			FileUtils.forceMkdir(new File(file, LOG_FILE_PATH));
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			isInit = true;
		} catch (IOException e) {
			isInit = false;
		}
		LOGGER.debug("Got log " + logFile);
	}

	public void addCommitToJsonLog(final Commit commit) {

		if (!isInit) { return; }
		
		final ObjectMapper mapper = new ObjectMapper();

		final List<Commit> commits = getLoggedCommits();

		for (final Commit c : commits) {
			if (c.isTheSame(commit)) { return; }
		}

		commits.add(commit);

		try {
			mapper.writeValue(logFile, commits);
		} catch (final Exception e) {
			LOGGER.debug("Error occurred during saving to log file", e);
		}
	}

	public Commit getCommit(final long id) {

		if (!isInit) { return null; }

		for (final Commit commit : getLoggedCommits()) {
			if (commit.getId().equals(String.valueOf(id))) { return commit; }
		}

		return null;

	}

	private List<Commit> getLoggedCommits() {

		final ObjectMapper mapper = new ObjectMapper();
		final TypeReference<List<Commit>> ref = new TypeReference<List<Commit>>() {
		};
		List<Commit> commits = Lists.newArrayList();

		if (!isInit) { return commits; }

		try {
			commits = mapper.readValue(logFile, ref);
		} catch (final Exception e) {
			LOGGER.trace("Doesn't exist yet", e);
		}

		return commits;

	}
	
}
