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
public class HttpHeaders {

    /*****************************
     * Header keys *
     ****************************/

    public static final String AUTHORIZATION = "Authorization";

    public static final String CONTENT_TYPE = "Content-Type";

    /** @since 3.1 */
    public static final String CONTENT_LENGTH = "Content-Length";

    public static final String DEPTH = "depth";

    public static final String NUXEO_TX_TIMEOUT = "Nuxeo-Transaction-Timeout";

    public static final String NX_USER = "NX_USER";

    public static final String NX_TOKEN = "NX_TOKEN";

    public static final String NX_RD = "NX_RD";

    public static final String NX_TS = "NX_TS";

    /** @since 3.1 */
    public static final String USER_AGENT = "User-Agent";

    public static final String X_AUTHENTICATION_TOKEN = "X-Authentication-Token";

    public static final String X_PROPERTIES = "X-NXproperties";

    public static final String X_VOID_OPERATION = "X-NXVoidOperation";

    public static final String X_VERSIONING_OPTION = "X-Versioning-Option";

    /****************************
     * Full header values *
     ****************************/

    public static final String CONTENT_TYPE_APPLICATION_OCTET_STREAM = CONTENT_TYPE + ": "
            + MediaTypes.APPLICATION_OCTET_STREAM_S;
}
