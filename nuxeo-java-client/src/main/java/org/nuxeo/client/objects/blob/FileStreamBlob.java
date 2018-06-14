/*
 * (C) Copyright 2018 Nuxeo (http://nuxeo.com/) and others.
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.nuxeo.client.spi.NuxeoClientException;
import org.nuxeo.client.util.IOUtils;

/**
 * @since 3.1
 * @deprecated since 3.1, this blob should not be used. It is used internally to bring backward compatibility since
 *             fetchBlob API deprecation.
 */
@Deprecated
public class FileStreamBlob extends FileBlob {

    protected final InputStream inputStream;

    public FileStreamBlob(InputStream inputStream) {
        super(null, null, -1);
        this.inputStream = inputStream;
    }

    public FileStreamBlob(InputStream inputStream, String filename, String mimeType, long length) {
        super(filename, mimeType, length);
        this.inputStream = inputStream;
    }

    @Override
    public InputStream getStream() throws IOException {
        if (file == null) {
            // return the input stream
            return inputStream;
        } else {
            // input stream has been read
            return super.getStream();
        }
    }

    @Override
    public File getFile() {
        convertStreamToFileIfNeeded();
        return super.getFile();
    }

    @Override
    public void track() {
        // trigger conversion to file ¯\_(ツ)_/¯
        convertStreamToFileIfNeeded();
        super.track();
    }

    protected void convertStreamToFileIfNeeded() {
        if (file == null) {
            // IOUtils.copyToTempFile close the input stream for us
            try {
                file = IOUtils.copyToTempFile(inputStream);
            } catch (IOException e) {
                throw new NuxeoClientException("Error during deserialization of input stream", e);
            }
        }
    }
}