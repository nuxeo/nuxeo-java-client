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

import org.nuxeo.client.ConstantsV1;
import org.nuxeo.client.objects.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 0.1
 */
public class Directory extends Entity {

    public Directory() {
        super(ConstantsV1.ENTITY_TYPE_DIRECTORY);
    }

    @JsonProperty("entries")
    protected List<DirectoryEntry> directoryEntries = new ArrayList<>();

    public List<DirectoryEntry> getDirectoryEntries() {
        return directoryEntries;
    }

    public void setDirectoryEntries(List<DirectoryEntry> directoryEntries) {
        this.directoryEntries = directoryEntries;
    }

    public void addDirectoryEntry(DirectoryEntry directoryEntry) {
        this.directoryEntries.add(directoryEntry);
    }

}
