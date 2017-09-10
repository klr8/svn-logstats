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
