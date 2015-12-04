/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *          Nuxeo
 */
package org.nuxeo.java.client.api;

/**
 * @since 1.0
 */
public class ConstantsV1 {

    public static final String VERSION = "v1/";

    public static final String API_PATH = "/api/" + VERSION;

    public static final String ENTITY_TYPE_DOCUMENT = "document";

    public static final String HEADER_VOID_OPERATION = "X-NXVoidOperation";

    public static final String HEADER_TX_TIMEOUT = "Nuxeo-Transaction-Timeout";

    public static final String HEADER_PROPERTIES = "X-NXproperties";

    public static final String HEADER_ENRICHERS = "X-NXenrichers.document";

    public static final String HEADER_VERSIONING = "X-Versioning-Option";

    public static final String HEADER_FETCH = "X-NXfetch.document";

    public static final String HEADER_DEPTH = "depth";

    public static final String ENTITY_TYPE_DOCUMENTS = "documents";

    public static String ENTITY_TYPE_EXCEPTION = "exception";
}
