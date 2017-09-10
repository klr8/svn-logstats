package com.ervacon.svn.logstats;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link SvnLogEntryProcessor} that aggregates processed {@link SvnLogEntry log entries} and calculates a number of
 * interesting statistics.
 *
 * @author Erwin Vervaet
 */
public class SvnLogEntryAggregator implements SvnLogEntryProcessor {

	private final Map<String, SvnAuthorStats> statsPerAuthor = new HashMap<>();

	@Override
	public void process(SvnLogEntry logEntry) {
		statsPerAuthor.putIfAbsent(logEntry.author, new SvnAuthorStats(logEntry.author));
		SvnAuthorStats stats = statsPerAuthor.get(logEntry.author);
		stats.commits++;
		stats.pathsInCommits += logEntry.paths.size();
	}

	public int getTotalNumberOfCommits() {
		return statsPerAuthor.values().stream().mapToInt(stats -> stats.commits).sum();
	}

	public Set<String> getAuthors() {
		return statsPerAuthor.keySet();
	}

	public List<String> getAuthors(Comparator<SvnAuthorStats> cmp) {
		return statsPerAuthor.values().stream().sorted(cmp).map((stats) -> stats.author).collect(Collectors.toList());
	}

	public int getMostCommits() {
		return statsPerAuthor.values().stream().mapToInt(stats -> stats.commits).max().orElse(0);
	}

	public SvnAuthorStats getStatsFor(String author) {
		return statsPerAuthor.getOrDefault(author, new SvnAuthorStats(author));
	}

}
