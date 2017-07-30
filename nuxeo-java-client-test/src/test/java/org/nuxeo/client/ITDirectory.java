/*
 * (C) Copyright 2016-2017 Nuxeo (http://nuxeo.com/) and others.
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
 *     Vladimir Pasquier <vpasquier@nuxeo.com>
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.nuxeo.client.objects.directory.Directories;
import org.nuxeo.client.objects.directory.Directory;
import org.nuxeo.client.objects.directory.DirectoryEntries;
import org.nuxeo.client.objects.directory.DirectoryEntry;

/**
 * @since 0.1
 */
public class ITDirectory extends AbstractITBase {

    @Test
    public void itCanGetDirectories() {
        assumeTrue("itCanGetDirectories works only since Nuxeo 8.10",
                nuxeoClient.getServerVersion().isGreaterThan(NuxeoVersion.LTS_8_10));
        Directories directories = nuxeoClient.directoryManager().fetchDirectories();
        assertNotNull(directories);
        Directory continent = directories.getDirectory("continent");
        assertNotNull(continent);
        assertEquals("id", continent.getIdField());
        assertEquals("vocabulary", continent.getSchema());
        assertNull(continent.getParent());
        DirectoryEntries directoryEntries = continent.fetchEntries();
        assertNotNull(directoryEntries);
        assertEquals(7, directoryEntries.getDirectoryEntries().size());
    }

    @Test
    public void itCanGetDirectoryEntries() {
        DirectoryEntries directoryEntries = nuxeoClient.directoryManager().directory("continent").fetchEntries();
        assertNotNull(directoryEntries);
        assertEquals(7, directoryEntries.getDirectoryEntries().size());
    }

    @Test
    public void itCanCreateUpdateFetchDeleteDirectory() {
        Directory directory = nuxeoClient.directoryManager().directory("continent");

        // Create
        DirectoryEntry entry = new DirectoryEntry();
        entry.setDirectoryName("continent");
        entry.putIdProperty("test");
        entry.putLabelProperty("test");
        entry.putObsoleteProperty(0);
        entry.putOrderingProperty(0);
        DirectoryEntry result = directory.createEntry(entry);
        assertNotNull(result);
        assertEquals("continent", result.getDirectoryName());
        assertEquals("test", result.getLabelProperty());

        // Update
        result.putLabelProperty("new update");
        result = result.update();
        assertEquals("new update", result.getLabelProperty());

        // Fetch
        result = directory.fetchEntry("test");
        assertNotNull(result);
        assertEquals("continent", result.getDirectoryName());
        assertEquals("new update", result.getLabelProperty());

        // Another way to update
        Map<String, String> props = new HashMap<>();
        props.put(DirectoryEntry.ID_PROPERTY, "test");
        props.put(DirectoryEntry.LABEL_PROPERTY, "update again");
        result.setProperties(props);
        result = directory.updateEntry(result);
        assertNotNull(result);
        assertEquals("continent", result.getDirectoryName());
        assertEquals("update again", result.getLabelProperty());

        // Delete
        directory.deleteEntry(result.getIdPropertyString());
    }

}
