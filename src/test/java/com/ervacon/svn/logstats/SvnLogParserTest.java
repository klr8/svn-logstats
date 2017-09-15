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

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;
import java.time.temporal.ChronoField;
import javax.xml.stream.XMLStreamException;
import org.junit.Test;

public class SvnLogParserTest {

	@Test
	public void testSvnLogFileParsing() throws Exception {
		MockSvnLogEntryProcessor entryProcessor = new MockSvnLogEntryProcessor();
		SvnLogParser processor = new SvnLogParser(entryProcessor);
		try (Reader in = new StringReader(Util.readClassPathResource("/sampleSvnLogFile.xml"))) {
			processor.parse(in);
		}
		assertEquals(entryProcessor.getLogEntries().size(), 1);
		SvnLogEntry entry = entryProcessor.getLogEntries().get(0);
		assertEquals(21955, entry.revision);
		assertEquals("johnc", entry.author);
		assertEquals(2003, entry.date.get(ChronoField.YEAR));
		assertEquals(6, entry.date.get(ChronoField.MONTH_OF_YEAR));
		assertEquals(7, entry.date.get(ChronoField.DAY_OF_MONTH));
		assertEquals(12, entry.date.get(ChronoField.HOUR_OF_DAY));
		assertEquals(4, entry.date.get(ChronoField.MINUTE_OF_HOUR));
		assertEquals(4, entry.date.get(ChronoField.SECOND_OF_MINUTE));
		assertEquals(132787000, entry.date.get(ChronoField.NANO_OF_SECOND));
		assertEquals(2, entry.paths.size());
		assertEquals("M", entry.paths.get(0).action);
		assertEquals("file", entry.paths.get(0).kind);
		assertEquals("/trunk/quake/src/Quake.h", entry.paths.get(0).path);
		assertEquals("M", entry.paths.get(1).action);
		assertEquals("file", entry.paths.get(1).kind);
		assertEquals("/trunk/quake/src/Quake.c", entry.paths.get(1).path);
		assertEquals("Improve docs\n	    Alignment fixes", entry.msg);
	}

	@Test
	public void testSvnLogFileParsing_noLogEntries() throws Exception {
		MockSvnLogEntryProcessor entryProcessor = new MockSvnLogEntryProcessor();
		SvnLogParser processor = new SvnLogParser(entryProcessor);
		try (Reader in = new StringReader("<?xml version='1.0' encoding='UTF-8'?><log></log>")) {
			processor.parse(in);
		}
		assertEquals(0, entryProcessor.getLogEntries().size());
	}

	@Test
	public void testSvnLogFileParsing_badXml() throws Exception {
		MockSvnLogEntryProcessor entryProcessor = new MockSvnLogEntryProcessor();
		SvnLogParser processor = new SvnLogParser(entryProcessor);
		try (Reader in = new StringReader("this is not xml")) {
			processor.parse(in);
		} catch (XMLStreamException e) {
			// expected
		}
		assertEquals(0, entryProcessor.getLogEntries().size());
	}
}
