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

import java.io.File;

/**
 * SvnLogStats command line program.
 *
 * @author Erwin Vervaet
 */
public class SvnLogStats {

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("Usage: SvnLogStats logfile reportfile");
			System.out.println("\tlogfile\tThe path to the Subversion XML log file");
			System.out.println("\treportfile\tThe path of the HTML report file to write");
			System.exit(1);
		}

		File logFile = new File(args[0]).getCanonicalFile();
		if (!logFile.exists() && !logFile.canRead()) {
			System.out.println("Log file " + logFile + " does not exist or cannot be read");
			System.exit(1);
		}

		File reportFile = new File(args[1]).getCanonicalFile();
		if (reportFile.exists()) {
			System.out.println("Warning: report file " + reportFile + " exists and will be overwritten");
		}

		System.out.println("Reading " + logFile);
		SvnLogEntryAggregator aggregator = new SvnLogEntryAggregator();
		SvnLogParser parser = new SvnLogParser(aggregator);
		parser.parse(logFile);

		System.out.println("Processed " + aggregator.getTotalNumberOfCommits() + " commits made by "
				+ aggregator.getAuthors().size() + " authors");

		System.out.println("Writing HTML report to " + reportFile);
		new HtmlReportWriter(aggregator, reportFile).writeReport();
	}

}
