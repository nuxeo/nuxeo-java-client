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
        DirectoryEntries directoryEntries = nuxeoClient.directoryManager().fetchDirectoryEntries("continent");
        assertNotNull(directoryEntries);
        assertEquals(7, directoryEntries.getDirectoryEntries().size());
    }

    @Test
    public void itCanCreateUpdateFetchDeleteDirectory() {
        // Create
        DirectoryEntry entry = new DirectoryEntry();
        entry.setDirectoryName("continent");
        entry.putIdProperty(("test"));
        entry.putLabelProperty("test");
        entry.putObsoleteProperty(0);
        entry.putOrderingProperty(0);
        DirectoryEntry result = nuxeoClient.directoryManager().createDirectoryEntry(entry);
        assertNotNull(result);
        assertEquals("continent", result.getDirectoryName());
        assertEquals("test", result.getLabelProperty());

        // Update
        result.putLabelProperty("new update");
        result = result.update();
        assertEquals("new update", result.getLabelProperty());

        // Fetch
        // TODO no fetch in API ??
        // result = nuxeoClient.directory("continent").

        // Delete
        nuxeoClient.directoryManager().deleteDirectoryEntry(result);
    }

}
