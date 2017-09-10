package com.ervacon.svn.logstats;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * An entry in a Subversion log file.
 *
 * @author Erwin Vervaet
 */
public class SvnLogEntry {

	public int revision;
	public String author;
	public ZonedDateTime date;
	public List<SvnLogEntryPath> paths = new ArrayList<>();
	public String msg;

}
