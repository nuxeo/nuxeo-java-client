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
package org.nuxeo.client.api.marshaller;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.client.api.ConstantsV1;
import org.nuxeo.client.api.objects.Document;
import org.nuxeo.client.api.objects.Documents;
import org.nuxeo.client.api.objects.RecordSet;
import org.nuxeo.client.api.objects.blob.Blob;
import org.nuxeo.client.api.objects.blob.Blobs;
import org.nuxeo.client.internals.spi.NuxeoClientException;
import org.nuxeo.client.internals.util.IOUtils;
import org.nuxeo.client.internals.util.MediaType;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * @since 0.1
 */
public final class NuxeoResponseConverterFactory<T> implements Converter<ResponseBody, T> {

    private static final Logger logger = LogManager.getLogger(NuxeoResponseConverterFactory.class);

    protected JavaType javaType;

    protected NuxeoMarshaller<T> nuxeoMarshaller;

    protected final ObjectMapper objectMapper;

    protected ObjectReader adapter;

    NuxeoResponseConverterFactory(ObjectReader adapter, ObjectMapper objectMapper, JavaType javaType) {
        this.adapter = adapter;
        this.objectMapper = objectMapper;
        this.javaType = javaType;
    }

    NuxeoResponseConverterFactory(NuxeoMarshaller<T> nuxeoMarshaller, ObjectMapper objectMapper) {
        this.nuxeoMarshaller = nuxeoMarshaller;
        this.objectMapper = objectMapper;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        // Checking custom marshallers with the type of the method clientside.
        if (nuxeoMarshaller != null) {
            String response = extractJson(value);
            logger.debug(response);
            JsonParser jsonParser = objectMapper.getFactory().createParser(response);
            return nuxeoMarshaller.read(jsonParser);
        }
        // Checking if multipart outputs.
        MediaType mediaType = MediaType.parse(value.contentType().toString());
        if (!(mediaType.type().equals(ConstantsV1.APPLICATION) && mediaType.subtype().equals(ConstantsV1.JSON))
                && !(mediaType.type().equals(ConstantsV1.APPLICATION)
                        && mediaType.subtype().equals(ConstantsV1.JSON_NXENTITY))) {
            if (mediaType.type().equals(ConstantsV1.MULTIPART)) {
                Blobs blobs = new Blobs();
                try {
                    MimeMultipart mp = new MimeMultipart(
                            new ByteArrayDataSource(value.byteStream(), mediaType.toString()));
                    int size = mp.getCount();
                    for (int i = 0; i < size; i++) {
                        BodyPart part = mp.getBodyPart(i);
                        blobs.add(part.getFileName(), IOUtils.copyToTempFile(part.getInputStream()));
                    }
                } catch (MessagingException reason) {
                    throw new IOException(reason);
                }
                return (T) blobs;
            } else {
                return (T) new Blob(IOUtils.copyToTempFile(value.byteStream()));
            }
        }
        String nuxeoEntity = mediaType.nuxeoEntity();
        // Checking the type of the method clientside - aka object for Automation calls.
        if (javaType.getRawClass().equals(Object.class)) {
            if (nuxeoEntity != null) {
                switch (nuxeoEntity) {
                case ConstantsV1.ENTITY_TYPE_DOCUMENT:
                    return (T) readJSON(extractJson(value), Document.class);
                case ConstantsV1.ENTITY_TYPE_DOCUMENTS:
                    return (T) readJSON(extractJson(value), Documents.class);
                default:
                    return (T) value;
                }
            } else {
                String response = extractJson(value);
                Object objectResponse = readJSON(response, Object.class);
                if (objectResponse instanceof Map) {
                    Object entityType = ((Map<String, Object>) objectResponse).get(ConstantsV1.ENTITY_TYPE);
                    if (entityType != null) {
                        // Handle the legacy case when no 'entity-type' header has been set in the response but
                        // `entity-type` is written in the json payload as RecordSet objects
                        switch ((String) entityType) {
                        case ConstantsV1.ENTITY_TYPE_RECORDSET:
                            return (T) readJSON(response, RecordSet.class);
                        default:
                            return (T) response;
                        }
                    }
                    return (T) response;
                } else {
                    // Handle the cases when there is no `entity-type` in the json payload either in header
                    return (T) response;
                }
            }
        }
        Reader reader = value.charStream();
        try {
            return adapter.readValue(reader);
        } catch (IOException reason) {
            throw new NuxeoClientException(reason);
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

    public <T> T readJSON(Reader reader, Class javaType) {
        try {
            return (T) objectMapper.readValue(reader, javaType);
        } catch (IOException reason) {
            throw new NuxeoClientException("Converter Read Issue.", reason);
        }
    }

    public <T> T readJSON(String json, Class javaType) {
        try {
            return (T) objectMapper.readValue(json, javaType);
        } catch (IOException reason) {
            throw new NuxeoClientException("Converter Read Issue.", reason);
        }
    }

}
