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
package org.nuxeo.java.client.api.marshaller;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import retrofit2.Converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import okhttp3.ResponseBody;

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
