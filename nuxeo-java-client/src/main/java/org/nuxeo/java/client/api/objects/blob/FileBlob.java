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

import java.io.File;

import org.nuxeo.java.client.api.ConstantsV1;
import org.nuxeo.java.client.api.objects.Blob;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 1.0
 */
public class FileBlob extends Blob {

    protected final File file;

    public FileBlob() {
        super();
        file = null;
    }

    public FileBlob(File file) {
        super(file.getName(), ConstantsV1.APPLICATION_OCTET_STREAM);
        this.file = file;
    }

    @JsonIgnore
    @Override
    public int getLength() {
        long length = file.length();
        if (length > (long) Integer.MAX_VALUE) {
            return -1;
        }
        return (int) length;
    }

    public File getFile() {
        return file;
    }

}
