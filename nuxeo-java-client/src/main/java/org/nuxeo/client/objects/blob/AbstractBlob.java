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

/**
 * @since 3.0
 */
public abstract class AbstractBlob implements Blob {

    protected final String filename;

    protected final String mimeType;

    protected final long length;

    /**
     * @deprecated since 3.1, implementation should decide about length strategy
     */
    @Deprecated
    public AbstractBlob(String filename, String mimeType) {
        this(filename, mimeType, -1);
    }

    /**
     * @since 3.1
     */
    protected AbstractBlob(String filename, String mimeType, long length) {
        this.filename = filename;
        this.mimeType = mimeType;
        this.length = length;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    @Deprecated
    public int getLength() {
        long contentLength = getContentLength();
        if (contentLength > (long) Integer.MAX_VALUE) {
            return -1;
        }
        return (int) contentLength;
    }

    @Override
    public long getContentLength() {
        return length;
    }

}
