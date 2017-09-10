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

import java.util.Objects;

public class SvnAuthorStats {

	public String author;
	public int commits;
	public int pathsInCommits;

	public SvnAuthorStats(String author) {
		this.author = Objects.requireNonNull(author);
	}

	public int getAverageCommitSize() {
		return pathsInCommits / commits;
	}

	public static int orderByCommitsDesc(SvnAuthorStats stats1, SvnAuthorStats stats2) {
		return stats2.commits - stats1.commits;
	}
}