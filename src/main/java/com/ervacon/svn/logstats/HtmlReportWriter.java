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

import com.ervacon.svn.logstats.SvnLogEntryPath.PathAction;
import com.ervacon.svn.logstats.Util.KeyValuePair;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;

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

			out.println("<h2>Global statistics</h2>");
			writeSimpleChart(out, "Commit counts per author", null,
					stats -> new KeyValuePair(stats.author, stats.commits));
			writeSimpleChart(out, "Average commit size per author", null,
					stats -> new KeyValuePair(stats.author, stats.getAverageCommitSize()));
			writeCommitsPerHour(out);
			writeFileTypesInCommits(out, 20);
			writeSimpleChart(out, "Average commit message length per author", null,
					stats -> new KeyValuePair(stats.author, stats.getAverageMessageLength()));
			writeSimpleChart(out, "Empty commit messages per author", null,
					stats -> new KeyValuePair(stats.author, stats.emptyMsgs));
			writeSimpleChart(out, "Top 10 file add-ers", 10,
					stats -> new KeyValuePair(stats.author, stats.getActionCount(PathAction.A)));
			writeSimpleChart(out, "Top 10 file delete-ers", 10,
					stats -> new KeyValuePair(stats.author, stats.getActionCount(PathAction.D)));
			writeSimpleChart(out, "Top 10 file modify-ers", 10,
					stats -> new KeyValuePair(stats.author, stats.getActionCount(PathAction.M)));
			writeSimpleChart(out, "Top 10 file replace-ers", 10,
					stats -> new KeyValuePair(stats.author, stats.getActionCount(PathAction.R)));

			out.println("<h2>Author statistics</h2>");
			for (String author : aggregator.getAuthors()) {
				out.println("<a href='#" + author + "'>" + author + "</a> ");
			}
			for (SvnAuthorStats stats : aggregator.getStats()) {
				out.println("<a id='" + stats.author + "'/>");
				out.println("<h3>" + stats.author + "</h3>");

				out.println("<p>");
				out.println("Author " + stats.author + " was active between " + stats.firstCommit.format(ISO_DATE)
						+ " and " + stats.lastCommit.format(ISO_DATE) + ", and performed <b>" + stats.commits + "</b> commits.");
				out.println("The commits added " + stats.getActionCount(PathAction.A) + " files, removed "
						+ stats.getActionCount(PathAction.D) + " files, modified " + stats.getActionCount(PathAction.M)
						+ " files and replaced " + stats.getActionCount(PathAction.R) + " files.");
				out.println("</p>");

				writeCommitsPerHour(out, stats);
				writeFileTypesInCommits(out, stats, 10);
			};

			out.println("</body>");
			out.println("</html>");
		}
	}

	private void writeCommitsPerHour(PrintWriter out) throws IOException {
		List<KeyValuePair> data = new ArrayList<>();
		for (int i = 0; i < 24; i++) {
			data.add(new KeyValuePair(i + ":00", 0));
		}
		aggregator.getStats().forEach(stats -> {
			for (int i = 0; i < 24; i++) {
				data.get(i).value += stats.commitsPerHour[i];
			}
		});
		writeChart(out, "Commit time distribution", null, data);
	}

	private void writeCommitsPerHour(PrintWriter out, SvnAuthorStats stats) throws IOException {
		List<KeyValuePair> data = new ArrayList<>();
		for (int i = 0; i < 24; i++) {
			data.add(new KeyValuePair(i + ":00", stats.commitsPerHour[i]));
		}
		writeChart(out, "Commit time distribution for " + stats.author, null, data);
	}

	private void writeFileTypesInCommits(PrintWriter out, int limit) throws IOException {
		Map<String, Integer> fileTypesInCommits = new HashMap<>();
		aggregator.getStats().forEach(stats -> {
			stats.fileTypesInCommits.forEach((statsK, statsV) -> {
				fileTypesInCommits.compute(statsK, (k, v) -> v == null ? 1 : v + statsV);
			});
		});
		List<KeyValuePair> data = new ArrayList<>();
		fileTypesInCommits.forEach((k, v) -> data.add(new KeyValuePair(k, v)));
		data.sort(KeyValuePair::orderByValueDesc);
		writeChart(out, "File types in commits (top " + limit + ")", limit, data);
	}

	private void writeFileTypesInCommits(PrintWriter out, SvnAuthorStats stats, int limit) throws IOException {
		List<KeyValuePair> data = new ArrayList<>();
		stats.fileTypesInCommits.forEach((k, v) -> data.add(new KeyValuePair(k, v)));
		data.sort(KeyValuePair::orderByValueDesc);
		writeChart(out, "File types in commits for " + stats.author + " (top " + limit + ")", limit, data);
	}

	private void writeSimpleChart(PrintWriter out, String title, Integer limit, Function<SvnAuthorStats, KeyValuePair> mapper)
			throws IOException {
		List<KeyValuePair> data = aggregator.getStats().stream().map(mapper).collect(toList());
		data.sort(KeyValuePair::orderByValueDesc);
		writeChart(out, title, limit, data);
	}

	private void writeChart(PrintWriter out, String title, Integer limit, List<KeyValuePair> data) throws IOException {
		if (limit != null) {
			data = data.subList(0, Math.min(limit, data.size()));
		}

		int maxValue = data.stream().mapToInt(kv -> kv.value).max().orElse(0);

		if (maxValue == 0) {
			return;
		}

		out.println("<h4>" + title + "</h4>");
		out.println("<div class='chart'>");
		for (KeyValuePair kv : data) {
			int width = ((500 * kv.value) / maxValue);
			out.println("<div style='width: " + width + "px;'>" + kv.key + " (" + kv.value + ")</div>");
		}
		out.println("</div>");
	}
}
