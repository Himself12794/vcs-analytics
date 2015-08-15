# INTRODUCTION
__source-code-analytics-sdk__ is a repository for a Java SDK to capture all metrics about source code (Git and SVN)

To build, just run mvn clean install.

The binary with dependencies can be found on the releases page.

Here are the commands:

      analyze <url> - Gives statistics for a Git/SVN repository
        --branch=<branch> (Limits statistics to specified branch)
        --nostatistics (Indicates to just clone the repo)
        --builtin-analysis (Indicates to use builtin stat analysis)
        --username=<username> (Used for access to private repos)
        --password=<password> (Used for access to private repos)
        --start=<epoch-time> (Format: YYYY-MM-DDTHH:MM:SS+HH:MM)
        --end=<epoch-time> (Format: YYYY-MM-DDTHH:MM:SS+HH:MM)
        --rev-a=<SVN revision> (SVN only, reads information after this rev)
        --rev-b=<SVN revision> (SVN only, reads information before this rev)
        --nocommits (Indicates that only language information should be shown)
        --svn-source-only (SVN only, skips files that cloc does not consider source code)
        -s (forces the application to treat the url as a SVN repo)
        -g (forces the application to treat the url as a Git repo)

      help <command> - Shows help about the given command

      init  - Extracts CLOC resources

      debug - runs the program with some debug data
    Universal options:
      -d    - Enable debug logging
      -v    - Shows version
