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
package org.nuxeo.client.objects.directory;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.objects.Connectable;
import org.nuxeo.client.objects.Entity;
import org.nuxeo.client.objects.EntityTypes;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 0.1
 */
public class Directory extends Entity implements Connectable {

    @JsonProperty("entries")
    protected List<DirectoryEntry> directoryEntries = new ArrayList<>();

    public Directory() {
        super(EntityTypes.DIRECTORY);
    }

    public List<DirectoryEntry> getDirectoryEntries() {
        return directoryEntries;
    }

    public void setDirectoryEntries(List<DirectoryEntry> directoryEntries) {
        this.directoryEntries = directoryEntries;
    }

    public void addDirectoryEntry(DirectoryEntry directoryEntry) {
        this.directoryEntries.add(directoryEntry);
    }

    public DirectoryEntry getDirectoryEntry(String id) {
        for (DirectoryEntry directoryEntry : directoryEntries) {
            if (directoryEntry.getProperties().getId().equals(id)) {
                return directoryEntry;
            }
        }
        return null;
    }

    public DirectoryEntry getDirectoryEntry(int index) {
        return directoryEntries.get(index);
    }

    @Override
    public void reconnectWith(NuxeoClient nuxeoClient) {
        for (DirectoryEntry directoryEntry : directoryEntries) {
            directoryEntry.reconnectWith(nuxeoClient);
        }
    }

}
