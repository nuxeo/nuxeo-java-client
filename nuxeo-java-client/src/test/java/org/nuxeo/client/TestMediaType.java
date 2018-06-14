/*
 * (C) Copyright 2017-2018 Nuxeo (http://nuxeo.com/) and others.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;

import org.junit.Test;

/**
 * @since 3.0
 */
public class TestMediaType {

    /**
     * @since 3.1
     */
    @Test
    public void testMalformed() {
        MediaType mediaType = MediaType.parse("what");
        assertEquals("application", mediaType.type());
        assertEquals("octet-stream", mediaType.subtype());
    }

    @Test
    public void testTypeSubType() {
        MediaType mediaType = MediaType.parse("application/json");
        assertEquals("application", mediaType.type());
        assertEquals("json", mediaType.subtype());
    }

    @Test
    public void testTypeSubTypeCharset() {
        MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
        assertEquals("application", mediaType.type());
        assertEquals("json", mediaType.subtype());
        assertEquals(Charset.forName("UTF-8"), mediaType.charset());
    }

    @Test
    public void testEqualsTypeSubTypeWithoutSuffix() {
        MediaType mediaType1 = MediaType.parse("application/json; charset=UTF-8");
        MediaType mediaType2 = MediaType.parse("application/json+nxentity; charset=UTF-8");
        assertTrue(mediaType1.equalsTypeSubTypeWithoutSuffix(mediaType2));
        assertTrue(mediaType2.equalsTypeSubTypeWithoutSuffix(mediaType1));
    }

}
