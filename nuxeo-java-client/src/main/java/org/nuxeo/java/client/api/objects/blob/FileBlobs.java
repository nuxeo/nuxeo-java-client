/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *          Nuxeo
 */
package org.nuxeo.java.client.api.objects.blob;

import java.util.List;

import org.nuxeo.java.client.api.ConstantsV1;
import org.nuxeo.java.client.api.objects.Blob;
import org.nuxeo.java.client.api.objects.NuxeoObject;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 1.0
 */
public class FileBlobs extends NuxeoObject {

    public FileBlobs(String entityType) {
        super(ConstantsV1.ENTITY_TYPE_BLOBS);
    }

    @JsonProperty("entries")
    protected List<Blob> blobs;

    public FileBlobs(List<Blob> blobs) {
        super(ConstantsV1.ENTITY_TYPE_BLOBS);
        this.blobs = blobs;
    }
}
