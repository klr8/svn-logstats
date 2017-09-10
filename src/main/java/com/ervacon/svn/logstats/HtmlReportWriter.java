package com.ervacon.svn.logstats;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import static java.util.Objects.requireNonNull;

/**
 * Writes the statistics calculated by a {@link SvnLogEntryAggregator} to an HTML report file.
 *
 * @author Erwin Vervaet
 */
public class HtmlReportWriter {

	private final SvnLogEntryAggregator aggregator;
	private final File reportFile;

	public HtmlReportWriter(SvnLogEntryAggregator aggregator, File reportFile) {
		this.aggregator = requireNonNull(aggregator);
		this.reportFile = requireNonNull(reportFile);
	}

	public void writeReport() throws IOException {
		try (PrintWriter out = new PrintWriter(reportFile)) {
			out.println("<!doctype html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Subversion Commit Statistics</title>");
			out.println("<style>");
			out.println(Util.readClassPathResource("/style.css"));
			out.println("</style>");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Subversion Commit Statistics</h1>");

			out.println("<h2>Commit counts</h2>");
			out.println("<div class='chart'>");
			int mostCommits = aggregator.getMostCommits();
			for (String author : aggregator.getAuthors(SvnAuthorStats::orderByCommitsDesc)) {
				SvnAuthorStats stats = aggregator.getStatsFor(author);
				int value = ((500 * stats.commits) / mostCommits);
				out.println("<div style='width: " + value + "px;'>" + author + "</div>");
			}

			out.println("");
			out.println("</div>");

			out.println("</body>");
			out.println("</html>");
		}
	}
}
