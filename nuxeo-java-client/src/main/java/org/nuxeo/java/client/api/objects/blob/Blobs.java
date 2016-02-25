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
package org.nuxeo.java.client.api.objects.blob;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nuxeo.java.client.api.ConstantsV1;
import org.nuxeo.java.client.api.objects.NuxeoEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 0.1
 */
public class Blobs extends NuxeoEntity {

    public Blobs() {
        super(ConstantsV1.ENTITY_TYPE_BLOBS);
    }

    @JsonProperty("entries")
    protected List<Blob> blobs;

    public Blobs(List<Blob> blobs) {
        super(ConstantsV1.ENTITY_TYPE_BLOBS);
        this.blobs = blobs;
    }

    public List<Blob> getBlobs() {
        return blobs;
    }

    @JsonIgnore
    public int size(){
        return blobs.size();
    }
}
