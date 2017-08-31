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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.nuxeo.client.objects.Document;

import okhttp3.Response;

/**
 * @since 0.1
 */
public class ITSimpleClient extends AbstractITBase {

    @Test
    public void itCanGET() throws IOException {
        Response response = nuxeoClient.get(ITBase.BASE_URL + ConstantsV1.API_PATH + "path/");
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        String json = response.body().string();
        assertFalse(StringUtils.EMPTY.equals(json));
        Document document = nuxeoClient.getConverterFactory().readJSON(json, Document.class);
        assertNotNull(document);
        assertEquals("Root", document.getType());
    }

    @Test
    public void itCanPUT() throws IOException {
        Response response = nuxeoClient.put(ITBase.BASE_URL + ConstantsV1.API_PATH + "path/",
                "{\"entity-type\": \"document\",\"properties\": {\"dc:title\": \"new title\"}}");
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        String json = response.body().string();
        assertFalse(StringUtils.EMPTY.equals(json));
        Document document = nuxeoClient.getConverterFactory().readJSON(json, Document.class);
        assertNotNull(document);
        assertEquals("new title", document.getTitle());
    }

    @Test
    public void itCanPOSTAndDELETE() throws IOException {
        Response response = nuxeoClient.post(ITBase.BASE_URL + ConstantsV1.API_PATH + "path/",
                "{\"entity-type\": \"document\",\"type\":\"File\",\"name\":\"file\",\"properties\": {\"dc:title\": \"file\"}}");
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        String json = response.body().string();
        assertFalse(StringUtils.EMPTY.equals(json));
        Document document = nuxeoClient.getConverterFactory().readJSON(json, Document.class);
        assertNotNull(document);
        assertEquals("file", document.getTitle());

        response = nuxeoClient.delete(ITBase.BASE_URL + ConstantsV1.API_PATH + "path/file");
        assertTrue(response.isSuccessful());
        json = response.body().string();
        assertTrue(StringUtils.EMPTY.equals(json));
    }

}
