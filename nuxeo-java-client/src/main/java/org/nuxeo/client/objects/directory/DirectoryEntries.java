/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
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
 *         Vladimir Pasquier <vpasquier@nuxeo.com>
 */
package org.nuxeo.client.objects.directory;

import java.util.List;

import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.objects.Connectable;
import org.nuxeo.client.objects.Entities;
import org.nuxeo.client.objects.EntityTypes;

/**
 * @since 0.1
 */
public class DirectoryEntries extends Entities<DirectoryEntry> implements Connectable {

    public DirectoryEntries() {
        super(EntityTypes.DIRECTORY_ENTRIES);
    }

    public DirectoryEntries(List<? extends DirectoryEntry> entries) {
        super(EntityTypes.DIRECTORY_ENTRIES, entries);
    }

    public <T> DirectoryEntry getDirectoryEntry(T id) {
        for (DirectoryEntry directoryEntry : entries) {
            if (directoryEntry.getId().equals(String.valueOf(id))) {
                return directoryEntry;
            }
        }
        return null;
    }

    @Override
    public void reconnectWith(NuxeoClient nuxeoClient) {
        for (DirectoryEntry directoryEntry : entries) {
            directoryEntry.reconnectWith(nuxeoClient);
        }
    }

}
