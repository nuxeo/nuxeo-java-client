/*
 * (C) Copyright 2017-2018 Nuxeo (http://nuxeo.com/) and others.
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
package org.nuxeo.client.objects.blob;

import static org.nuxeo.client.MediaTypes.APPLICATION_OCTET_STREAM_S;

import java.io.InputStream;

/**
 * @since 3.0
 */
public class StreamBlob extends AbstractBlob {

    private final transient InputStream inputStream;

    public StreamBlob(InputStream inputStream, String filename) {
        this(inputStream, filename, APPLICATION_OCTET_STREAM_S);
    }

    public StreamBlob(InputStream inputStream, String filename, long length) {
        this(inputStream, filename, APPLICATION_OCTET_STREAM_S, length);
    }

    public StreamBlob(InputStream inputStream, String filename, String mimeType) {
        this(inputStream, filename, mimeType, -1);
    }

    public StreamBlob(InputStream inputStream, String filename, String mimeType, long length) {
        super(filename, mimeType, length);
        this.inputStream = inputStream;
    }

    @Override
    public InputStream getStream() {
        return inputStream;
    }
}
