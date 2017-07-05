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

import org.nuxeo.client.methods.DirectoryManagerAPI;
import org.nuxeo.client.objects.ConnectableEntity;
import org.nuxeo.client.objects.EntityTypes;

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

}
