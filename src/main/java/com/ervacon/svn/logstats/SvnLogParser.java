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

import static java.lang.Integer.parseInt;
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.ZonedDateTime;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Parser for Subversion log files in XML format.
 * <p>
 * Reads {@link SvnLogEntry} objects from an XML file and passes them on to a {@link SvnLogEntryProcessor} for further
 * processing. This parser is designed to be able to handle very large log files efficiently, so you should not have any
 * problems processing XML log files several hundreds of megabytes in size.
 * <p>
 * To produce an XML log file for your Subversion repository, issue a command like this:
 * <pre>
 *	svn log --xml https://svnhost.com/my/repo/path > logfile.xml
 * </pre>
 *
 * @see SvnLogEntryProcessor
 * @see SvnLogEntryAggregator
 *
 * @author Erwin Vervaet
 */
public class SvnLogParser {

	private final SvnLogEntryProcessor entryProcessor;

	/**
	 * Create a new parser which will push entries to given processor for processing.
	 *
	 * @param entryProcessor the processor to use to process log file entries
	 */
	public SvnLogParser(SvnLogEntryProcessor entryProcessor) {
		this.entryProcessor = requireNonNull(entryProcessor);
	}

	/**
	 * Parse given log file and pass read entries on to the configured processor for processing.
	 *
	 * @param logFile the Subversion log file
	 * @throws IOException when given file cannot be read
	 * @throws XMLStreamException when XML parsing fails
	 */
	public void parse(File logFile) throws IOException, XMLStreamException {
		try (Reader fin = new FileReader(logFile)) {
			parse(fin);
		}
	}

	/**
	 * Parse entries from given stream and pass them on to the configured processor.
	 *
	 * @param logFileReader the Subversion log file stream
	 * @throws XMLStreamException when XML parsing fails
	 */
	public void parse(Reader logFileReader) throws XMLStreamException {
		SvnLogEntry logEntry = null;
		SvnLogEntryPath logEntryPath = null;
		StringBuilder buf = null;

		// use StAX to process the XML log file
		XMLEventReader xmlEventReader = XMLInputFactory.newFactory().createXMLEventReader(logFileReader);
		try {
			/*
			<logentry revision="21955">
				<author>username</author>
				<date>2017-06-07T12:04:04.132787Z</date>
				<paths>
					<path action="M" kind="file">/trunk/path/to/file.c</path>
				</paths>
				<msg>Commit message</msg>
			</logentry>
			 */
			while (xmlEventReader.hasNext()) {
				XMLEvent xmlEvent = xmlEventReader.nextEvent();
				if (xmlEvent.isStartElement()) {
					StartElement startEl = xmlEvent.asStartElement();
					switch (startEl.getName().getLocalPart()) {
						case "logentry":
							logEntry = new SvnLogEntry();
							logEntry.revision = parseInt(startEl.getAttributeByName(new QName("revision")).getValue());
							break;

						case "author":
						case "date":
						case "msg":
							buf = new StringBuilder();
							break;

						case "path":
							logEntryPath = new SvnLogEntryPath();
							logEntryPath.action = startEl.getAttributeByName(new QName("action")).getValue();
							logEntryPath.kind = startEl.getAttributeByName(new QName("kind")).getValue();
							buf = new StringBuilder();
							break;
					}
				}
				if (buf != null && xmlEvent.isCharacters()) {
					buf.append(xmlEvent.asCharacters().getData());
				}
				if (xmlEvent.isEndElement()) {
					EndElement endEl = xmlEvent.asEndElement();
					switch (endEl.getName().getLocalPart()) {
						case "logentry":
							entryProcessor.process(logEntry);
							logEntry = null;
							break;

						case "author":
							logEntry.author = buf.toString().trim();
							buf = null;
							break;

						case "date":
							logEntry.date = ZonedDateTime.from(ISO_ZONED_DATE_TIME.parse(buf.toString().trim()));
							buf = null;
							break;

						case "path":
							logEntryPath.path = buf.toString().trim();
							logEntry.paths.add(logEntryPath);
							logEntryPath = null;
							buf = null;
							break;

						case "msg":
							logEntry.msg = buf.toString().trim();
							buf = null;
							break;
					}
				}
			}
		} finally {
			xmlEventReader.close();
		}
	}
}
