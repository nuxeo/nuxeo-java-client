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

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.objects.Connectable;
import org.nuxeo.client.objects.Entity;
import org.nuxeo.client.objects.EntityTypes;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 3.0
 */
public class Directories extends Entity implements Connectable {

    @JsonProperty("entries")
    protected List<Directory> directories = new ArrayList<>();

    public Directories() {
        super(EntityTypes.DIRECTORIES);
    }

    public List<Directory> getDirectories() {
        return directories;
    }

    public Directory getDirectory(String name) {
        for (Directory directory : directories) {
            if (directory.getName().equals(name)) {
                return directory;
            }
        }
        return null;
    }

    public Directory getDirectory(int index) {
        return directories.get(index);
    }

    @Override
    public void reconnectWith(NuxeoClient nuxeoClient) {
        for (Directory directory : directories) {
            directory.reconnectWith(nuxeoClient);
        }
    }

}
