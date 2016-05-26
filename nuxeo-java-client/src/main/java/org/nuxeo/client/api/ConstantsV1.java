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
package org.nuxeo.client.api;

import okhttp3.MediaType;

/**
 * @since 0.1
 */
public class ConstantsV1 {

    public static final String VERSION = "v1/";

    public static final String API_PATH = "/api/" + VERSION;

    public static final String ENTITY_TYPE_DOCUMENT = "document";

    public static final String ENTITY_TYPE_WORKFLOW = "workflow";

    public static final String ENTITY_TYPE_GRAPH = "graph";

    public static final String ENTITY_TYPE_WORKFLOWS = "workflows";

    public static final String ENTITY_TYPE_RECORDSET = "recordSet";

    public static final String DEFAULT_DOC_TYPE = "File";

    public static final String ENTITY_TYPE_LOGIN = "login";

    public static final String HEADER_VOID_OPERATION = "X-NXVoidOperation";

    public static final String HEADER_TX_TIMEOUT = "Nuxeo-Transaction-Timeout";

    public static final String HEADER_PROPERTIES = "X-NXproperties";

    public static final String HEADER_ENRICHERS = "X-NXenrichers.document";

    public static final String HEADER_VERSIONING = "X-Versioning-Option";

    public static final String HEADER_FETCH = "X-NXfetch.document";

    public static final String HEADER_DEPTH = "depth";

    public static final String ENTITY_TYPE_DOCUMENTS = "documents";

    public static final String ENTITY_TYPE_AUDIT = "logEntries";

    public static final String ENTITY_TYPE_LOGENTRY = "logEntry";

    public static final String ENTITY_TYPE_ACP = "acls";

    public static final String ENTITY_TYPE_USER = "user";

    public static final String ENTITY_TYPE_USERS = "users";

    public static final String ENTITY_TYPE_GROUPS = "groups";

    public static final String ENTITY_TYPE_DIRECTORY = "directoryEntries";

    public static final String ENTITY_TYPE_DIRECTORY_ENTRY = "directoryEntry";

    public static final String ENTITY_TYPE_GROUP = "group";

    public static final String ENTITY_TYPE_BLOBS = "blobs";

    public static final MediaType APPLICATION_JSON_CHARSET_UTF_8 = MediaType.parse("application/json; charset=UTF-8");

    public static final String ENTITY_TYPE_EXCEPTION = "exception";

    public static final String ENTITY_TYPE_OPERATION = "operation";

    public static final String ENTITY_TYPE_TASK = "task";

    public static final String ENTITY_TYPE_TASKS = "tasks";

    public static final String ENTITY_TYPE = "entity-type";

    public static final MediaType APPLICATION_JSON = MediaType.parse("application/json");

    public static final String APPLICATION = "application";

    public static final String JSON = "json";

    public static final String MULTIPART = "multipart";

    public static final String JSON_NXENTITY = "json+nxentity";

    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    public static final String CONTENT_TYPE_APPLICATION_OCTET_STREAM = "Content-Type: " + APPLICATION_OCTET_STREAM;

    public static final String MD_5 = "MD5";

    public static final int CHUNK_SIZE = 1024 * 1024;

    public static final String UPLOAD_CHUNKED_TYPE = "chunked";

    public static final String UPLOAD_NORMAL_TYPE = "normal";

    public static final String DEFAULT_FILE_CONTENT = "file:content";

}
