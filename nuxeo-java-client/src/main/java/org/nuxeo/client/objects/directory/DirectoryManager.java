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

import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.methods.DirectoryManagerAPI;
import org.nuxeo.client.objects.AbstractConnectable;

/**
 * @since 0.1
 */
public class DirectoryManager extends AbstractConnectable<DirectoryManagerAPI> {

    public DirectoryManager(NuxeoClient nuxeoClient) {
        super(DirectoryManagerAPI.class, nuxeoClient);
    }

    public Directory fetchDirectory(String directoryName) {
        return fetchResponse(api.fetchDirectory(directoryName));
    }

    public Directory fetchDirectory(String directoryName, String currentPageIndex, String pageSize, String maxResults,
            String sortBy, String sortOrder) {
        return fetchResponse(
                api.fetchDirectory(directoryName, currentPageIndex, pageSize, maxResults, sortBy, sortOrder));
    }

    public DirectoryEntry createDirectoryEntry(String directoryName, DirectoryEntry directoryEntry) {
        return fetchResponse(api.createDirectoryEntry(directoryName, directoryEntry));
    }

    public DirectoryEntry updateDirectoryEntry(String directoryName, String directoryEntryId,
            DirectoryEntry directoryEntry) {
        return fetchResponse(api.updateDirectoryEntry(directoryName, directoryEntryId, directoryEntry));
    }

    public void deleteDirectoryEntry(String directoryName, String directoryEntryId) {
        fetchResponse(api.deleteDirectoryEntry(directoryName, directoryEntryId));
    }

}
