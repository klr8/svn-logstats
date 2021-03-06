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

/**
 * Strategy to process {@link SvnLogEntry} objects read by a {@link SvnLogParser}. Implementation should typically avoid
 * holding on the processed log entries because a typical Subversion log file will contain many such entries,
 * potentially leading to out-of-memory problems.
 *
 * @see SvnLogParser
 *
 * @author Erwin Vervaet
 */
public interface SvnLogEntryProcessor {

	/**
	 * Process given log entry.
	 *
	 * @param logEntry the log entry to process.
	 */
	void process(SvnLogEntry logEntry);
}
