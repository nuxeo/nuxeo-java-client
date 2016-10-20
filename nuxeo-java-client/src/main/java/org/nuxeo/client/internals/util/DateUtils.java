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
 *     Nuxeo - initial API and implementation
 */
package org.nuxeo.client.internals.util;

import java.util.Date;

/**
 * @since 1.0
 */
public class DateUtils {

    private DateUtils() {
    }

    public static Date parseDate(String date) {
        return DateParser.parseW3CDateTime(date);
    }

    public static String formatDate(Date date) {
        return DateParser.formatW3CDateTime(date);
    }

}
