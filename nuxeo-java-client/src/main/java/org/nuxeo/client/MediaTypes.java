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
 * @since 3.0
 */
public class MediaTypes {

    public static final String APPLICATION_JSON_S = "application/json";

    public static final MediaType APPLICATION_JSON = MediaType.parse(APPLICATION_JSON_S);

    public static final String APPLICATION_JSON_NXENTITY_S = "application/json+nxentity";

    public static final MediaType APPLICATION_JSON_NXENTITY = MediaType.parse(APPLICATION_JSON_NXENTITY_S);

    public static final String APPLICATION_JSON_CHARSET_UTF_8_S = "application/json; charset=UTF-8";

    public static final MediaType APPLICATION_JSON_CHARSET_UTF_8 = MediaType.parse(APPLICATION_JSON_CHARSET_UTF_8_S);

    public static final String APPLICATION_OCTET_STREAM_S = "application/octet-stream";

    public static final MediaType APPLICATION_OCTET_STREAM = MediaType.parse(APPLICATION_OCTET_STREAM_S);

    public static final String APPLICATION_NUXEO_EMPTY_LIST_S = "application/nuxeo-empty-list";

    public static final MediaType APPLICATION_NUXEO_EMPTY_LIST = MediaType.parse(APPLICATION_NUXEO_EMPTY_LIST_S);

    public static final String MULTIPART_S = "multipart";

    private MediaTypes() {
        // empty
    }

}
