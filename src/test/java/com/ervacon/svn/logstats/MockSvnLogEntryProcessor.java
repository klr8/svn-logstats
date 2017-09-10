package com.ervacon.svn.logstats;

import java.util.ArrayList;
import java.util.List;

public class MockSvnLogEntryProcessor implements SvnLogEntryProcessor {

	private final List<SvnLogEntry> logEntries = new ArrayList<>();

	@Override
	public void process(SvnLogEntry logEntry) {
		logEntries.add(logEntry);
	}

	public List<SvnLogEntry> getLogEntries() {
		return logEntries;
	}

}
