/*
 * (C) Copyright 2017 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @since 3.0
 */
public class TestNuxeoVersion {

    @Test
    public void testParseVersion() {
        assertVersion(NuxeoVersion.parse("7.10"), 7, 10, 0, false);
        assertVersion(NuxeoVersion.parse("7.10-HF05"), 7, 10, 5, false);
        assertVersion(NuxeoVersion.parse("7.10-HF05-SNAPSHOT"), 7, 10, 5, true);
        assertVersion(NuxeoVersion.parse("7.10-SNAPSHOT"), 7, 10, 0, true);
    }

    @Test
    public void testVersion() {
        assertEquals("7.10", new NuxeoVersion(7, 10, 0, false).version());
        assertEquals("7.10-HF05", new NuxeoVersion(7, 10, 5, false).version());
        assertEquals("7.10-HF05-SNAPSHOT", new NuxeoVersion(7, 10, 5, true).version());
        assertEquals("7.10-SNAPSHOT", new NuxeoVersion(7, 10, 0, true).version());
    }

    @Test
    public void testGreaterThan() {
        // Compare majors
        assertTrue(NuxeoVersion.LTS_7_10.isGreaterThan(NuxeoVersion.LTS_7_10));
        assertTrue(NuxeoVersion.LTS_8_10.isGreaterThan(NuxeoVersion.LTS_7_10));
        assertFalse(NuxeoVersion.LTS_7_10.isGreaterThan(NuxeoVersion.LTS_8_10));

        // Compare minor
        assertTrue(NuxeoVersion.LTS_7_10.isGreaterThan(new NuxeoVersion(7, 1, 0, false)));
        assertFalse(new NuxeoVersion(7, 1, 0, false).isGreaterThan(NuxeoVersion.LTS_7_10));

        // Compare hotfix
        assertTrue(NuxeoVersion.LTS_7_10.hotfix(5).isGreaterThan(NuxeoVersion.LTS_7_10.hotfix(5)));
        assertTrue(NuxeoVersion.LTS_7_10.hotfix(5).isGreaterThan(NuxeoVersion.LTS_7_10));
        assertFalse(NuxeoVersion.LTS_7_10.isGreaterThan(NuxeoVersion.LTS_7_10.hotfix(5)));

        // Compare both
        assertTrue(new NuxeoVersion(8, 1, 0, false).isGreaterThan(NuxeoVersion.LTS_7_10));
        assertFalse(NuxeoVersion.LTS_7_10.isGreaterThan(new NuxeoVersion(8, 1, 0, false)));
        assertTrue(NuxeoVersion.LTS_8_10.hotfix((5)).isGreaterThan(NuxeoVersion.LTS_7_10.hotfix(10)));
    }

    public void assertVersion(NuxeoVersion version, int majorVersion, int minorVersion, int hotfix, boolean snapshot) {
        assertEquals(version.majorVersion(), majorVersion);
        assertEquals(version.minorVersion(), minorVersion);
        assertEquals(version.hotfix(), hotfix);
        assertEquals(version.snapshot(), snapshot);
    }

}
