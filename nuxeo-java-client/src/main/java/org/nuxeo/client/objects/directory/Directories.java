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

import java.util.List;

import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.objects.Connectable;
import org.nuxeo.client.objects.Entities;
import org.nuxeo.client.objects.EntityTypes;

/**
 * @since 3.0
 */
public class Directories extends Entities<Directory> implements Connectable {

    public Directories() {
        super(EntityTypes.DIRECTORIES);
    }

    /**
     * @since 3.2
     */
    public Directories(List<? extends Directory> entries) {
        super(EntityTypes.DIRECTORIES, entries);
    }

    /**
     * @deprecated since 3.2, use {@link #getEntries()} instead
     */
    @Deprecated
    public List<Directory> getDirectories() {
        return getEntries();
    }

    public Directory getDirectory(String name) {
        for (Directory directory : entries) {
            if (directory.getName().equals(name)) {
                return directory;
            }
        }
        return null;
    }

    /**
     * @deprecated since 3.2, use {@link #getEntry(int)} instead
     */
    @Deprecated
    public Directory getDirectory(int index) {
        return super.getEntry(index);
    }

    @Override
    public void reconnectWith(NuxeoClient nuxeoClient) {
        for (Directory directory : entries) {
            directory.reconnectWith(nuxeoClient);
        }
    }

}
