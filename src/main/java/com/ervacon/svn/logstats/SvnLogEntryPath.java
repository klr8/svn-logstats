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
 * A path affected by a {@link SvnLogEntry}.
 *
 * @author Erwin Vervaet
 */
public class SvnLogEntryPath {

	public PathAction action;
	public PathKind kind;
	public String path; // simple String to avoid problems interpreting paths on file systems we might not know about

	public static enum PathAction {
		M, // modified
		D, // deleted
		A, // added
		R // replaced
	}

	public static enum PathKind {
		FILE, DIR
	}
}
