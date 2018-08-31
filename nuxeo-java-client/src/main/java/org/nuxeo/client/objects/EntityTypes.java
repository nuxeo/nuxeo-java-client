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
package org.nuxeo.client.objects;

/**
 * @since 3.0
 */
public class EntityTypes {

    public static final String ACP = "acls";

    /**
     * This API is available since Nuxeo Server 10.2.
     *
     * @since 3.1
     */
    public static final String ANNOTATION = "annotation";

    /**
     * This API is available since Nuxeo Server 10.2.
     *
     * @since 3.1
     */
    public static final String ANNOTATIONS = "annotations";

    /**
     * This API is available since Nuxeo Server 10.3.
     *
     * @since 3.2
     */
    public static final String COMMENT = "comment";

    /**
     * This API is available since Nuxeo Server 10.3.
     *
     * @since 3.2
     */
    public static final String COMMENTS = "comments";

    public static final String AUDIT = "logEntries";

    public static final String BLOBS = "blobs";

    public static final String DIRECTORIES = "directories";

    public static final String DIRECTORY = "directory";

    public static final String DIRECTORY_ENTRIES = "directoryEntries";

    public static final String DIRECTORY_ENTRY = "directoryEntry";

    public static final String DOCUMENT = "document";

    public static final String DOCUMENTS = "documents";

    public static final String DOC_TYPE = "docType";

    public static final String DOC_TYPES = "docTypes";

    public static final String EXCEPTION = "exception";

    public static final String FACET = "facet";

    public static final String GRAPH = "graph";

    public static final String GROUP = "group";

    public static final String GROUPS = "groups";

    public static final String LOGENTRY = "logEntry";

    public static final String LOGIN = "login";

    public static final String OPERATION = "operation";

    public static final String RECORDSET = "recordSet";

    public static final String SCHEMA = "schema";

    public static final String TASK = "task";

    public static final String TASKS = "tasks";

    public static final String USER = "user";

    public static final String USERS = "users";

    public static final String WORKFLOW = "workflow";

    public static final String WORKFLOWS = "workflows";

    private EntityTypes() {
        // empty
    }

}
