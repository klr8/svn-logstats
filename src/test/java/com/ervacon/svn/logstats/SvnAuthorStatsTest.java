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
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SvnAuthorStatsTest {

	@Test
	public void testUpdateWith() {
		SvnAuthorStats stats = new SvnAuthorStats("test");
		stats.updateWith(newLogEntry("test", ZonedDateTime.now(), "Test", "Afile1", "Dfile2", "Mfile3", "Rfile4"));
		assertEquals(stats.author, "test");
		assertEquals(stats.commits, 1);
		// TODO
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
