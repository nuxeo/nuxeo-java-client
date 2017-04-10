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
package org.nuxeo.client.marshaller;

import java.io.IOException;

import org.nuxeo.client.MediaTypes;
import org.nuxeo.client.spi.NuxeoClientException;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * @since 0.1
 */
public final class NuxeoRequestConverterFactory<T> implements Converter<T, RequestBody> {

    protected final JavaType javaType;

    protected final ObjectMapper objectMapper;

    NuxeoRequestConverterFactory(ObjectMapper objectMapper, JavaType javaType) {
        this.objectMapper = objectMapper;
        this.javaType = javaType;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        byte[] bytes = objectMapper.writeValueAsBytes(value);
        return RequestBody.create(MediaTypes.APPLICATION_JSON_CHARSET_UTF_8.toOkHttpMediaType(), bytes);
    }

    public String writeJSON(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException reason) {
            throw new NuxeoClientException("Converter Write Issue. See NuxeoRequestConverterFactory#writeJSON", reason);
        }
    }

}
