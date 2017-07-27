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
public class DirectoryManager extends AbstractConnectable<DirectoryManagerAPI, DirectoryManager> {

    public DirectoryManager(NuxeoClient nuxeoClient) {
        super(DirectoryManagerAPI.class, nuxeoClient);
    }

    /**
     * CAUTION: Only available for Nuxeo Server greater than LTS 2016 - 8.10
     */
    public Directories fetchDirectories() {
        return fetchResponse(api.fetchDirectories());
    }

    /**
     * @return a new {@link Directory} instance to make remote calls on it
     * @since 3.0
     */
    public Directory directory(String directoryName) {
        return new Directory(nuxeoClient, directoryName);
    }

}
