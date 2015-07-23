package com.cisco.dft.sdk.vcs.repo;

import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.ISVNReporter;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDiffWindow;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNRepo extends Repo {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("SVNRepo");

	private final SVNRepository theRepo;

	public SVNRepo(String url) throws SVNException {
		this(url, "username", "password");
	}

	public SVNRepo(String url, String username, String password) throws SVNException {
		
		theRepo = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager( username, password.toCharArray() );
		theRepo.setAuthenticationManager( authManager );
		repoInfo.setName(guessName(url));
		sync();
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void sync() {
		
		System.out.println("synchronizing");
		
		DAVRepositoryFactory.setup();
		
		long startRevision = 0L;
		long endRevision = -1L; 
		
		try {
		
			BranchInfo bi = repoInfo.getBranchInfo("master");
			
			bi.resetInfo();
			
			System.out.println("logging");
			             
			Collection<SVNLogEntry> logEntries = theRepo.log( new String[] { "" } , null , startRevision , endRevision , true , true );
			
			bi.incrementCommitCount(logEntries.size());
			
			for (SVNLogEntry leEntry : logEntries) { 
				
				String author = leEntry.getAuthor();
				AuthorInfo ai = bi.getAuthorInfo(author);
				
				ai.add(new AuthorCommit(Long.toString(leEntry.getRevision()), leEntry.getDate(), 0, 0, 0, false, leEntry.getMessage().replace("\n", " ")));
				
				
			}
			
			updateRepoInfo(bi);
			
		} catch (Exception e) {
			LOGGER.error("An error occured in creating the repo.", e);
		}
		
	}
	
	private void updateRepoInfo(BranchInfo bi) throws SVNException {
		System.out.println("Statusing");
		
		theRepo.status(SVNRevision.HEAD.getNumber(), "", SVNDepth.INFINITY, REPORTER, EDITOR);
		
		System.out.println("Done statusing");

	}

	@Override
	public List<String> getBranches() {
		return null;
	}
	
	private static final ISVNReporterBaton REPORTER = new ISVNReporterBaton() {
		
		@Override
		public void report(ISVNReporter reporter) throws SVNException {
			reporter.setPath("", null, SVNRevision.HEAD.getNumber(), SVNDepth.INFINITY, true);
			reporter.finishReport();
			
		}
	};
	
	private static final ISVNEditor EDITOR = new ISVNEditor() {
		
        @Override
        public void targetRevision(long revision) throws SVNException {
        }

        @Override
        public void openRoot(long revision) throws SVNException {
        }

        @Override
        public void deleteEntry(String path, long revision) throws SVNException {
        }

        @Override
        public void absentDir(String path) throws SVNException {
        }

        @Override
        public void absentFile(String path) throws SVNException {
        }

        @Override
        public void addDir(String path, String copyFromPath, long copyFromRevision) throws SVNException {
            System.out.println("Directory: " + path);
        }

        @Override
        public void openDir(String path, long revision) throws SVNException {
        }

        @Override
        public void changeDirProperty(String name, SVNPropertyValue value) throws SVNException {
        }

        @Override
        public void closeDir() throws SVNException {
        }

        @Override
        public void addFile(String path, String copyFromPath, long copyFromRevision) throws SVNException {
            System.out.println("File: " + path);
        }

        @Override
        public void openFile(String path, long revision) throws SVNException {
        }

        @Override
        public void changeFileProperty(String path, String propertyName, SVNPropertyValue propertyValue) throws SVNException {
        }

        @Override
        public void closeFile(String path, String textChecksum) throws SVNException {
        }

        @Override
        public SVNCommitInfo closeEdit() throws SVNException {
            return null;
        }

        @Override
        public void abortEdit() throws SVNException {
        }

        @Override
        public void applyTextDelta(String path, String baseChecksum) throws SVNException {
        }

        @Override
        public OutputStream textDeltaChunk(String path, SVNDiffWindow diffWindow) throws SVNException { return null; }

        @Override
        public void textDeltaEnd(String path) throws SVNException {}
    };

}
