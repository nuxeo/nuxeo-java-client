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
        Directories directories = nuxeoClient.directoryManager().fetchDirectories();
        assertNotNull(directories);
        Directory continent = directories.getDirectory("continent");
        assertNotNull(continent);
        assertEquals("id", continent.getIdField());
        assertEquals("vocabulary", continent.getSchema());
        assertNull(continent.getParent());
        DirectoryEntries directoryEntries = continent.fetchEntries();
        assertNotNull(directoryEntries);
        assertEquals(7, directoryEntries.size());
    }

    @Test
    public void itCanGetDirectoryEntries() {
        DirectoryEntries directoryEntries = nuxeoClient.directoryManager().directory("continent").fetchEntries();
        assertNotNull(directoryEntries);
        assertEquals(7, directoryEntries.size());
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
        entry.putOrderingProperty(0L);
        DirectoryEntry result = directory.createEntry(entry);
        assertNotNull(result);
        assertEquals(Long.valueOf(0L), result.getOrderingProperty());
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
        directory.deleteEntry(result.getId());
    }

    @Test
    public void itCanSetOrderingPropertyInteger() {
        Directory directory = nuxeoClient.directoryManager().directory("continent");

        DirectoryEntry entryWithInteger = new DirectoryEntry();
        entryWithInteger.setDirectoryName("continent");
        entryWithInteger.putIdProperty("test1");
        entryWithInteger.putLabelProperty("test1");
        entryWithInteger.putObsoleteProperty(0);

        entryWithInteger.putOrderingProperty(0L);
        DirectoryEntry resultWithInteger = directory.createEntry(entryWithInteger);
        assertNotNull(resultWithInteger);
        assertEquals(Long.valueOf(0L), resultWithInteger.getOrderingProperty());
        // assert the previous value and set a new Integer one
        assertEquals(Long.valueOf(0L), resultWithInteger.putOrderingProperty(Long.valueOf(Integer.MAX_VALUE)));

        resultWithInteger = resultWithInteger.update();
        assertEquals(Long.valueOf(Integer.MAX_VALUE), resultWithInteger.getOrderingProperty());
        // assert the previous value
        assertEquals(Long.valueOf(Integer.MAX_VALUE), resultWithInteger.putOrderingProperty(0L));

        resultWithInteger = resultWithInteger.update();
        assertEquals(Long.valueOf(0L), resultWithInteger.getOrderingProperty());
    }

    @Test
    public void itCanSetOrderingPropertyLong() {
        Directory directory = nuxeoClient.directoryManager().directory("continent");

        DirectoryEntry entryWithLong = new DirectoryEntry();
        entryWithLong.setDirectoryName("continent");
        entryWithLong.putIdProperty("test2");
        entryWithLong.putLabelProperty("test2");
        entryWithLong.putObsoleteProperty(1);

        entryWithLong.putOrderingProperty(0L);
        DirectoryEntry resultWithLong = directory.createEntry(entryWithLong);
        assertNotNull(resultWithLong);
        assertEquals(Long.valueOf(0L), resultWithLong.getOrderingProperty());
        // assert the previous value and set a new Long one
        assertEquals(Long.valueOf(0L), resultWithLong.putOrderingProperty(Long.MAX_VALUE));

        resultWithLong = resultWithLong.update();
        assertEquals(Long.valueOf(Long.MAX_VALUE), resultWithLong.getOrderingProperty());
        // assert the previous value
        assertEquals(Long.valueOf(Long.MAX_VALUE), resultWithLong.putOrderingProperty(0L));

        resultWithLong = resultWithLong.update();
        assertEquals(Long.valueOf(0L), resultWithLong.getOrderingProperty());
    }

}
