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
package org.nuxeo.client.objects.blob;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.nuxeo.client.objects.Entity;
import org.nuxeo.client.objects.EntityTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 0.1
 */
public class Blobs extends Entity {

    public Blobs() {
        super(null);
    }

    @JsonProperty("entries")
    protected List<Blob> blobs = new ArrayList<>();

    public Blobs(List<Blob> blobs) {
        super(EntityTypes.BLOBS);
        this.blobs = blobs;
    }

    public List<Blob> getBlobs() {
        return blobs;
    }

    @JsonIgnore
    public int size() {
        return blobs.size();
    }

    @JsonIgnore
    public void add(File file) {
        Blob blob = new Blob(file);
        blobs.add(blob);
    }

    @JsonIgnore
    public void add(String fileName, File file) {
        Blob blob = new Blob(file);
        blob.setFileName(fileName);
        blobs.add(blob);
    }

}
