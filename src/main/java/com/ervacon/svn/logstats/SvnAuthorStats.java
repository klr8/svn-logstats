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

import static com.ervacon.svn.logstats.Util.getFilenameExtension;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Aggregated statistics for a specific author found in the Subversion log.
 */
public class SvnAuthorStats {

	public final String author;
	public int commits = 0;
	public int[] commitsPerHour = new int[24];
	public int pathsInCommits = 0;
	public Map<String, Integer> fileTypesInCommits = new HashMap<>();
	public int emptyMsgs = 0;
	public int msgLength = 0;
	public ZonedDateTime firstCommit;
	public ZonedDateTime lastCommit;

	public SvnAuthorStats(String author) {
		this.author = Objects.requireNonNull(author);
	}

	public void updateWith(SvnLogEntry logEntry) {
		commits++;
		commitsPerHour[logEntry.date.get(ChronoField.HOUR_OF_DAY)]++;
		pathsInCommits += logEntry.paths.size();
		Set<String> extensionsInCommit = new HashSet<>();
		logEntry.paths.forEach(path -> {
			String extension = getFilenameExtension(path.path);
			if (extension == null) {
				extension = "other";
			}
			extensionsInCommit.add(extension);
		});
		extensionsInCommit.forEach(extension -> fileTypesInCommits.compute(extension, (k, v) -> v == null ? 1 : v + 1));
		if (logEntry.msg == null || logEntry.msg.trim().length() == 0) {
			emptyMsgs++;
		} else {
			msgLength += logEntry.msg.length();
		}
		if (firstCommit == null || firstCommit.isAfter(logEntry.date)) {
			firstCommit = logEntry.date;
		}
		if (lastCommit == null || lastCommit.isBefore(logEntry.date)) {
			lastCommit = logEntry.date;
		}
	}

	public int getAverageCommitSize() {
		return pathsInCommits / commits;
	}

	public int getAverageMessageLength() {
		return msgLength / commits;
	}

	public static int sortByAuthorNameAsc(SvnAuthorStats stats1, SvnAuthorStats stats2) {
		return stats1.author.compareTo(stats2.author);
	}
}
