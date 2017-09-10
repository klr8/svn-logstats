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
}
