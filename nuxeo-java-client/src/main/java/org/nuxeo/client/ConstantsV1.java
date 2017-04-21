/*
 * (C) Copyright 2016-2017 Nuxeo (http://nuxeo.com/) and others.
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
package org.nuxeo.client;

/**
 * @since 0.1
 */
public class ConstantsV1 {

    public static final String VERSION = "v1/";

    public static final String API_PATH = "/api/" + VERSION;

    public static final String DEFAULT_DOC_TYPE = "File";

    public static final String ENTITY_TYPE = "entity-type";

    public static final String MD_5 = "MD5";

    public static final int CHUNK_SIZE = 1024 * 1024;

    public static final String UPLOAD_CHUNKED_TYPE = "chunked";

    public static final String UPLOAD_NORMAL_TYPE = "normal";

}
