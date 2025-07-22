/*
 * (C) Copyright 2025 Nuxeo (http://nuxeo.com/) and others.
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
 *     Kevin Leturc <kevin.leturc@hyland.com>
 */
package org.nuxeo.client.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @since 4.0.2
 */
public final class PathSegments {

    private PathSegments() {
        // factory class
    }

    public static String encode(String segment) {
        String encoded = URLEncoder.encode(segment, StandardCharsets.UTF_8);
        // better encode ' ' instead of '+' which is a valid character in Nuxeo path segments
        encoded = encoded.replace("+", "%20");
        // put back '/' character
        encoded = encoded.replace("%2F", "/");
        return encoded;
    }
}
