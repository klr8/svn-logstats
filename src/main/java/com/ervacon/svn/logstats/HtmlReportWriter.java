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

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.Objects.requireNonNull;

import com.ervacon.svn.logstats.Util.KeyValuePair;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			writeCommitsPerAuthor(out);
			writeAvgCommitSizePerAuthor(out);
			writeCommitsPerHour(out);
			writeFileTypesInCommits(out, 20);
			writeAvgMessageLength(out);
			writeCommitsPerAuthorWithNoMessage(out);
			// top adders, removers, modifyers

			out.println("<h2>Author statistics</h2>");
			for (String author : aggregator.getAuthors()) {
				out.println("<a href='#" + author + "'>" + author + "</a> ");
			}
			for (SvnAuthorStats stats : aggregator.getStats()) {
				out.println("<a id='" + stats.author + "'/>");
				out.println("<h3>" + stats.author + "</h3>");

				out.println("<p>Author " + stats.author + " was active between " + stats.firstCommit.format(ISO_DATE)
						+ " and " + stats.lastCommit.format(ISO_DATE) + ", and performed <b>"
						+ stats.commits + "</b> commits.</p>");

				writeCommitsPerHour(out, stats);
				writeFileTypesInCommits(out, stats, 10);
			};

			out.println("</body>");
			out.println("</html>");
		}
	}

	private void writeCommitsPerAuthor(PrintWriter out) throws IOException {
		List<KeyValuePair> data = new ArrayList<>();
		aggregator.getStats().forEach(stats -> data.add(new KeyValuePair(stats.author, stats.commits)));
		data.sort(KeyValuePair::orderByValueDesc);
		writeChart(out, "Commit counts per author", data);
	}

	private void writeAvgCommitSizePerAuthor(PrintWriter out) throws IOException {
		List<KeyValuePair> data = new ArrayList<>();
		aggregator.getStats().forEach(stats -> data.add(new KeyValuePair(stats.author, stats.getAverageCommitSize())));
		data.sort(KeyValuePair::orderByValueDesc);
		writeChart(out, "Average commit size per author", data);
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
		writeChart(out, "Commit time distribution", data);
	}

	private void writeCommitsPerHour(PrintWriter out, SvnAuthorStats stats) throws IOException {
		List<KeyValuePair> data = new ArrayList<>();
		for (int i = 0; i < 24; i++) {
			data.add(new KeyValuePair(i + ":00", stats.commitsPerHour[i]));
		}
		writeChart(out, "Commit time distribution for " + stats.author, data);
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
		writeChart(out, "File types in commits (top " + limit + ")", data.subList(0, Math.min(limit, data.size())));
	}

	private void writeFileTypesInCommits(PrintWriter out, SvnAuthorStats stats, int limit) throws IOException {
		List<KeyValuePair> data = new ArrayList<>();
		stats.fileTypesInCommits.forEach((k, v) -> data.add(new KeyValuePair(k, v)));
		data.sort(KeyValuePair::orderByValueDesc);
		writeChart(out, "File types in commits for " + stats.author + " (top " + limit + ")", data.subList(0, Math.min(limit, data.size())));
	}

	private void writeAvgMessageLength(PrintWriter out) throws IOException {
		List<KeyValuePair> data = new ArrayList<>();
		aggregator.getStats().forEach(stats -> data.add(new KeyValuePair(stats.author, stats.getAverageMessageLength())));
		data.sort(KeyValuePair::orderByValueDesc);
		writeChart(out, "Average commit message length per author", data);
	}

	private void writeCommitsPerAuthorWithNoMessage(PrintWriter out) throws IOException {
		List<KeyValuePair> data = new ArrayList<>();
		aggregator.getStats().forEach(stats -> data.add(new KeyValuePair(stats.author, stats.emptyMsgs)));
		data.sort(KeyValuePair::orderByValueDesc);
		writeChart(out, "Empty commit messages per author", data);
	}

	private void writeChart(PrintWriter out, String title, List<KeyValuePair> data) throws IOException {
		int maxValue = data.stream().mapToInt(kv -> kv.value).max().orElse(0);

		out.println("<h4>" + title + "</h4>");
		out.println("<div class='chart'>");
		for (KeyValuePair kv : data) {
			int width = ((500 * kv.value) / maxValue);
			out.println("<div style='width: " + width + "px;'>" + kv.key + " (" + kv.value + ")</div>");
		}
		out.println("</div>");
	}
}
