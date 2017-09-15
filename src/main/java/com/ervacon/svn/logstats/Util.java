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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * General purpose utility methods.
 *
 * @author Erwin Vervaet
 */
public final class Util {

	private Util() {
	}

	public static String readClassPathResource(String name) throws IOException {
		try (BufferedReader bin = new BufferedReader(new InputStreamReader(Util.class.getResourceAsStream(name)))) {
			return bin.lines().collect(Collectors.joining("\n"));
		}
	}

	public static String getFilenameExtension(String path) {
		int idx = path.lastIndexOf('.');
		if (idx == -1) {
			return null;
		}
		return path.substring(idx + 1);
	}

	/**
	 * Simple key-value pair.
	 */
	public static class KeyValuePair {

		public String key;
		public int value;

		public KeyValuePair(String key, int value) {
			this.key = key;
			this.value = value;
		}

		public static int orderByValueDesc(KeyValuePair kv1, KeyValuePair kv2) {
			return kv2.value - kv1.value;
		}
	}
}
