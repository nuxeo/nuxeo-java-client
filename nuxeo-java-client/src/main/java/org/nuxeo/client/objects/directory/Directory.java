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
package org.nuxeo.client.objects.directory;

import java.util.Objects;

import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.methods.DirectoryManagerAPI;
import org.nuxeo.client.objects.ConnectableEntity;
import org.nuxeo.client.objects.EntityTypes;
import org.nuxeo.client.util.PathSegments;

/**
 * @since 3.0
 */
public class Directory extends ConnectableEntity<DirectoryManagerAPI, Directory> {

    private String name;

    private String schema;

    private String idField;

    private String parent;

    public Directory() {
        super(EntityTypes.DIRECTORY, DirectoryManagerAPI.class);
    }

    public Directory(NuxeoClient nuxeoClient, String name) {
        super(EntityTypes.DIRECTORY, DirectoryManagerAPI.class, nuxeoClient);
        this.name = Objects.requireNonNull(name, "Directory name must not be null");
    }

    public String getName() {
        return name;
    }

    public String getSchema() {
        return schema;
    }

    public String getIdField() {
        return idField;
    }

    public String getParent() {
        return parent;
    }

    public DirectoryEntries fetchEntries() {
        return fetchResponse(api.fetchDirectoryEntries(name));
    }

    public DirectoryEntries fetchEntries(String currentPageIndex, String pageSize, String maxResults, String sortBy,
            String sortOrder) {
        return fetchResponse(
                api.fetchDirectoryEntries(name, currentPageIndex, pageSize, maxResults, sortBy, sortOrder));
    }

    public DirectoryEntry createEntry(DirectoryEntry entry) {
        entry.setDirectoryName(name);
        return fetchResponse(api.createDirectoryEntry(name, entry));
    }

    /**
     * @since 3.12.0
     */
    public DirectoryEntry fetchEntry(Object entryId) {
        Objects.requireNonNull(entryId, "The entryId can not be null.");
        return fetchResponse(api.fetchDirectoryEntry(name, PathSegments.encode(String.valueOf(entryId))));
    }

    public DirectoryEntry fetchEntry(String entryId) {
        Objects.requireNonNull(entryId, "The entryId can not be null.");
        return fetchResponse(api.fetchDirectoryEntry(name, PathSegments.encode(entryId)));
    }

    public DirectoryEntry updateEntry(DirectoryEntry entry) {
        entry.setDirectoryName(name);
        String entryId = Objects.requireNonNull(entry.getId(), "You have to give the entry id to your entry.");
        return fetchResponse(api.updateDirectoryEntry(name, PathSegments.encode(entryId), entry));
    }

    /**
     * @since 3.12.0
     */
    public void deleteEntry(Object entryId) {
        Objects.requireNonNull(entryId, "The entryId can not be null.");
        fetchResponse(api.deleteDirectoryEntry(name, PathSegments.encode(String.valueOf(entryId))));
    }

    public void deleteEntry(String entryId) {
        Objects.requireNonNull(entryId, "The entryId can not be null.");
        fetchResponse(api.deleteDirectoryEntry(name, PathSegments.encode(entryId)));
    }

}
