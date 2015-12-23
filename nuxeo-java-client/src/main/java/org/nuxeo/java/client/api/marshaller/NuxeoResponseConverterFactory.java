/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and contributors.
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
package org.nuxeo.java.client.api.marshaller;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import retrofit.Converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.squareup.okhttp.ResponseBody;

/**
 * @since 1.0
 */
public final class NuxeoResponseConverterFactory<T> implements Converter<ResponseBody, T> {

    private static final Logger logger = LogManager.getLogger(NuxeoResponseConverterFactory.class);

    protected NuxeoMarshaller<T> nuxeoMarshaller;

    protected ObjectMapper objectMapper;

    protected ObjectReader adapter;

    NuxeoResponseConverterFactory(ObjectReader adapter) {
        this.adapter = adapter;
    }

    NuxeoResponseConverterFactory(NuxeoMarshaller<T> nuxeoMarshaller, ObjectMapper objectMapper) {
        this.nuxeoMarshaller = nuxeoMarshaller;
        this.objectMapper = objectMapper;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        if (nuxeoMarshaller != null) {
            // TODO JAVACLIENT-26
            String response = value.string();
            logger.debug(response);
            JsonParser jsonParser = objectMapper.getFactory().createParser(response);
            return nuxeoMarshaller.read(jsonParser);
        }
        Reader reader = value.charStream();
        try {
            return adapter.readValue(reader);
        } finally {
            closeQuietly(reader);
        }
    }

    static void closeQuietly(Closeable closeable) {
        if (closeable == null)
            return;
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }
}
