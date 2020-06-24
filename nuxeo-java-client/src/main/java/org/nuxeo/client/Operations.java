/*
 * (C) Copyright 2017-2020 Nuxeo (http://nuxeo.com/) and others.
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
package org.nuxeo.client;

/**
 * Some operation id declarations.
 *
 * @since 3.0
 */
public class Operations {

    public static final String BLOB_ATTACH_ON_DOCUMENT = "Blob.AttachOnDocument";

    /**
     * @since 3.1
     */
    public static final String DIRECTORY_ENTRIES = "Directory.Entries";

    public static final String DOCUMENT_ADD_PERMISSION = "Document.AddPermission";

    public static final String DOCUMENT_REMOVE_PERMISSION = "Document.RemovePermission";

    /**
     * CAUTION: Only available for Nuxeo Server greater than LTS 2016 - 8.10
     */
    public static final String DOCUMENT_REMOVE_PROXIES = "Document.RemoveProxies";

    /**
     * @since 3.6
     */
    public static final String DOCUMENT_CHECK_IN = "Document.CheckIn";

    /**
     * @since 3.6
     */
    public static final String DOCUMENT_GET_LAST_VERSION = "Document.GetLastVersion";

    /**
     * @since 3.1
     */
    public static final String DOCUMENT_GET_BLOB = "Document.GetBlob";

    /**
     * @since 3.1
     */
    public static final String DOCUMENT_GET_BLOBS = "Document.GetBlobs";

    /**
     * @since 3.1
     */
    public static final String DOCUMENT_GET_BLOBS_BY_PROPERTY = "Document.GetBlobsByProperty";

    /**
     * This operation is available since Nuxeo 10.1.
     * 
     * @since 3.1
     */
    public static final String DOCUMENT_TRASH = "Document.Trash";

    /**
     * This operation is available since Nuxeo 10.1.
     * 
     * @since 3.1
     */
    public static final String DOCUMENT_UNTRASH = "Document.Untrash";

    /**
     * @since 3.1
     */
    public static final String DOCUMENT_UPDATE = "Document.Update";

    /**
     * This operation is available since {@link NuxeoVersion#LTS_8_10}.
     *
     * @since 3.1
     */
    public static final String ES_WAIT_FOR_INDEXING = "Elasticsearch.WaitForIndexing";

    /**
     * @since 3.1
     */
    public static final String REPOSITORY_GET_DOCUMENT = "Repository.GetDocument";

    private Operations() {
        // no instance allowed
    }
}
