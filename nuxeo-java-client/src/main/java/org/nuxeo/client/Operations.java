/*
 * (C) Copyright 2017 Nuxeo (http://nuxeo.com/) and others.
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

    public static final String DOCUMENT_ADD_PERMISSION = "Document.AddPermission";

    public static final String DOCUMENT_REMOVE_PERMISSION = "Document.RemovePermission";

    /**
     * CAUTION: Only available for Nuxeo Server greater than LTS 2016 - 8.10
     */
    public static final String DOCUMENT_REMOVE_PROXIES = "Document.RemoveProxies";

    /**
     * This operation is available since Nuxeo 10.1.
     * 
     * @since 3.1
     */
    public static final String TRASH_DOCUMENT = "Document.Trash";

    /**
     * This operation is available since Nuxeo 10.1.
     * 
     * @since 3.1
     */
    public static final String UNTRASH_DOCUMENT = "Document.Untrash";

}
