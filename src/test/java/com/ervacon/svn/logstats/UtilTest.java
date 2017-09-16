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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;

public class UtilTest {

	@Test
	public void testReadClassPathResource() throws Exception {
		assertNotNull(Util.readClassPathResource("/style.css"));
	}

	@Test
	public void testGetFilenameExtension() {
		assertEquals("java", Util.getFilenameExtension("Test.java"));
		assertEquals("java", Util.getFilenameExtension("/foo/bar/Test.java"));
		assertEquals("java", Util.getFilenameExtension("/foo/bar-1.0.0/Test.java"));
		assertNull(Util.getFilenameExtension("/foo/bar/startup"));
	}
}
