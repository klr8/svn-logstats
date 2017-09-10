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
