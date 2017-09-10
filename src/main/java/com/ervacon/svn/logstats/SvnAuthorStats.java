package com.ervacon.svn.logstats;

import java.util.Objects;

public class SvnAuthorStats {

	public String author;
	public int commits;
	public int pathsInCommits;

	public SvnAuthorStats(String author) {
		this.author = Objects.requireNonNull(author);
	}

	public int getAverageCommitSize() {
		return pathsInCommits / commits;
	}

	public static int orderByCommitsDesc(SvnAuthorStats stats1, SvnAuthorStats stats2) {
		return stats2.commits - stats1.commits;
	}
}
