/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ervacon.svn.logstats;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		statsPerAuthor.get(logEntry.author).updateWith(logEntry);
	}

	public int getTotalNumberOfCommits() {
		return statsPerAuthor.values().stream().mapToInt(stats -> stats.commits).sum();
	}

	public List<String> getAuthors() {
		return getStats().stream().map(stats -> stats.author).collect(toList());
	}

	public List<SvnAuthorStats> getStats() {
		List<SvnAuthorStats> stats = new ArrayList(statsPerAuthor.values());
		stats.sort(SvnAuthorStats::sortByAuthorNameAsc);
		return stats;
	}

	public SvnAuthorStats getStatsFor(String author) {
		return statsPerAuthor.getOrDefault(author, new SvnAuthorStats(author));
	}
}
