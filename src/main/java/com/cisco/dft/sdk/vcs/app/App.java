package com.cisco.dft.sdk.vcs.app;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.dft.sdk.vcs.repo.GitRepo;

public final class App {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("Application");
	
	public static App app;

	private final ProgramConfig config;
	
	App(ProgramConfig config) {
		this.config = config;
	}
	
	public ProgramConfig getConfig() {
		return config;
	}
	
	/**
	 * Runs the program with the given config parameters.
	 * 
	 * @throws GitAPIException
	 */
	public void execute() throws GitAPIException {
		
		LOGGER.info("Executing with params: " + config.toString());
		
		switch (config.getAction()) {
			case ANALYZE:
				analyze();
				break;
			case HELP:
				help();
				break;
			case INIT:
				init();
				break;
			default:
				help();
				break;
			
		}
		
	}
	
	public void init() {
		Cloc.init();
	}
	
	public void help() {
		System.out.println(ProgramConfig.getUsage());
	}
	
	public void analyze() throws GitAPIException {
		
		if (config.getUrl() == null) {
			
			System.err.println("No URL specified. Usage: analyze --url=<url>");
			return;
			
		} else {

			Cloc.init();
			
			GitRepo repo = new GitRepo(config.getUrl(), "all".equals(config.getBranch()) ? null : config.getBranch(), config.shouldGenerateStats());
			
			System.out.println(repo);

			repo.close();
			
		}
		
	}

	public static void main(String[] args) throws Exception {

		app = new App(ProgramConfig.parseArgMap(ArgParser.getArgMap(args)));
		app.execute();
		
		// TODO cloc analysis - full testing
		// TODO clean output

	}

}
