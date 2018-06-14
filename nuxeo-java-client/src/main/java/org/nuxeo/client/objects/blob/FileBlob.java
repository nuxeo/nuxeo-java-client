/*
 * (C) Copyright 2016-2018 Nuxeo (http://nuxeo.com/) and others.
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileCleaningTracker;
import org.nuxeo.client.MediaTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 0.1
 */
public class FileBlob extends AbstractBlob {

    protected final File file;

    public FileBlob(File file) {
        this(file, file.getName());
    }

    public FileBlob(File file, String filename) {
        this(file, filename, MediaTypes.APPLICATION_OCTET_STREAM_S);
    }

    public FileBlob(File file, String filename, String mediaType) {
        super(filename, mediaType);
        this.file = file;
    }

    @Override
    public int getLength() {
        long length = file.length();
        if (length > (long) Integer.MAX_VALUE) {
            return -1;
        }
        return (int) length;
    }

    @Override
    public InputStream getStream() throws IOException {
        return new FileInputStream(file);
    }

    public File getFile() {
        return file;
    }

    public void track() {
        if (file != null) {
            FileCleaningTracker fileCleaningTracker = new FileCleaningTracker();
            fileCleaningTracker.track(file, this);
        }
    }

    @JsonIgnore
    protected String formatLength(int len) {
        int k = len / 1024;
        if (k <= 0) {
            return len + " B";
        } else if (k < 1024) {
            return k + " K";
        } else {
            return (k / 1024) + " M";
        }
    }

    @Override
    public String toString() {
        return filename + " - " + mimeType + " - " + formatLength(getLength());
    }

}
