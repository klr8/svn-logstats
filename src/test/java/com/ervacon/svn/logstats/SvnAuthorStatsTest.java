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

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SvnAuthorStatsTest {

	@Test
	public void testUpdateWith() {
		ZonedDateTime now = ZonedDateTime.now();

		SvnAuthorStats stats = new SvnAuthorStats("test");
		assertEquals(stats.author, "test");

		stats.updateWith(newLogEntry("test", now, "Test", "Afile1.c", "Dfile2.h", "Mfile3.c", "Rfile4"));

		assertEquals(stats.commits, 1);
		for (int i = 0; i < 24; i++) {
			if (i == now.get(ChronoField.HOUR_OF_DAY)) {
				assertEquals(1, stats.commitsPerHour[i]);
			} else {
				assertEquals(0, stats.commitsPerHour[i]);
			}
		}
		assertEquals(4, stats.pathsInCommits);
		assertEquals(3, stats.fileTypesInCommits.size());
		assertEquals(2, stats.fileTypesInCommits.get("c").intValue());
		assertEquals(1, stats.fileTypesInCommits.get("h").intValue());
		assertEquals(1, stats.fileTypesInCommits.get("other").intValue());
		assertEquals(0, stats.emptyMsgs);
		assertEquals(4, stats.msgLength);
		assertEquals(now, stats.firstCommit);
		assertEquals(now, stats.lastCommit);
		assertEquals(4, stats.actionCounts.size());
		assertEquals(1, stats.actionCounts.get(SvnLogEntryPath.PathAction.A).intValue());
		assertEquals(1, stats.actionCounts.get(SvnLogEntryPath.PathAction.D).intValue());
		assertEquals(1, stats.actionCounts.get(SvnLogEntryPath.PathAction.M).intValue());
		assertEquals(1, stats.actionCounts.get(SvnLogEntryPath.PathAction.R).intValue());

		stats.updateWith(newLogEntry("test", now.plusHours(1), null, "Mfile3.c"));

		assertEquals(stats.commits, 2);
		for (int i = 0; i < 24; i++) {
			if (i == now.get(ChronoField.HOUR_OF_DAY) || i - 1 == now.get(ChronoField.HOUR_OF_DAY)) {
				assertEquals(1, stats.commitsPerHour[i]);
			} else {
				assertEquals(0, stats.commitsPerHour[i]);
			}
		}
		assertEquals(5, stats.pathsInCommits);
		assertEquals(3, stats.fileTypesInCommits.size());
		assertEquals(3, stats.fileTypesInCommits.get("c").intValue());
		assertEquals(1, stats.fileTypesInCommits.get("h").intValue());
		assertEquals(1, stats.fileTypesInCommits.get("other").intValue());
		assertEquals(1, stats.emptyMsgs);
		assertEquals(4, stats.msgLength);
		assertEquals(now, stats.firstCommit);
		assertEquals(now.plusHours(1), stats.lastCommit);
		assertEquals(4, stats.actionCounts.size());
		assertEquals(1, stats.actionCounts.get(SvnLogEntryPath.PathAction.A).intValue());
		assertEquals(1, stats.actionCounts.get(SvnLogEntryPath.PathAction.D).intValue());
		assertEquals(2, stats.actionCounts.get(SvnLogEntryPath.PathAction.M).intValue());
		assertEquals(1, stats.actionCounts.get(SvnLogEntryPath.PathAction.R).intValue());
	}

	@Test
	public void testGetAverageCommitSize() {
		SvnAuthorStats stats = new SvnAuthorStats("test");
		stats.updateWith(newLogEntry("test", ZonedDateTime.now(), "Testing", "Afile1"));
		stats.updateWith(newLogEntry("test", ZonedDateTime.now(), "Testing", "Afile2", "Afile3", "Afile4"));
		assertEquals(2, stats.getAverageCommitSize());
	}

	@Test
	public void testGetAverageMessageLength() {
		SvnAuthorStats stats = new SvnAuthorStats("test");
		stats.updateWith(newLogEntry("test", ZonedDateTime.now(), "Test", "Mfile1"));
		stats.updateWith(newLogEntry("test", ZonedDateTime.now(), "Tester", "Mfile1"));
		assertEquals(5, stats.getAverageMessageLength());
	}

	@Test
	public void testGetActionCount() {
		SvnAuthorStats stats = new SvnAuthorStats("test");
		stats.updateWith(newLogEntry("test", ZonedDateTime.now(), "Test", "Afile1", "Dfile2", "Mfile3", "Rfile4"));
		stats.updateWith(newLogEntry("test", ZonedDateTime.now(), "Tester", "Mfile1"));
		assertEquals(1, stats.getActionCount(SvnLogEntryPath.PathAction.A));
		assertEquals(1, stats.getActionCount(SvnLogEntryPath.PathAction.D));
		assertEquals(2, stats.getActionCount(SvnLogEntryPath.PathAction.M));
		assertEquals(1, stats.getActionCount(SvnLogEntryPath.PathAction.R));
	}

	private SvnLogEntry newLogEntry(String author, ZonedDateTime date, String msg, String... paths) {
		SvnLogEntry logEntry = new SvnLogEntry();
		logEntry.revision = 1;
		logEntry.author = author;
		logEntry.date = date;
		logEntry.msg = msg;
		if (paths != null) {
			for (String path : paths) {
				SvnLogEntryPath logEntryPath = new SvnLogEntryPath();
				logEntryPath.action = SvnLogEntryPath.PathAction.valueOf(path.substring(0, 1));
				logEntryPath.kind = SvnLogEntryPath.PathKind.FILE;
				logEntryPath.path = path;
				logEntry.paths.add(logEntryPath);
			}
		}
		return logEntry;
	}
}
