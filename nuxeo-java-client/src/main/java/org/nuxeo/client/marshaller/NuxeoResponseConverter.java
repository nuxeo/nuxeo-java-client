/*
 * (C) Copyright 2016-2018 Nuxeo (http://nuxeo.com/) and others.
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.client.ConstantsV1;
import org.nuxeo.client.MediaType;
import org.nuxeo.client.MediaTypes;
import org.nuxeo.client.Responses;
import org.nuxeo.client.objects.blob.Blob;
import org.nuxeo.client.objects.blob.Blobs;
import org.nuxeo.client.objects.blob.FileBlob;
import org.nuxeo.client.objects.blob.FileStreamBlob;
import org.nuxeo.client.objects.blob.StreamBlob;
import org.nuxeo.client.spi.NuxeoClientException;
import org.nuxeo.client.util.IOUtils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * @since 0.1
 */
public final class NuxeoResponseConverter<T> implements Converter<ResponseBody, T> {

    protected final JavaType javaType;

    protected final ObjectMapper objectMapper;

    protected final Map<String, Class<?>> entityTypeToClass;

    protected NuxeoResponseConverter(ObjectMapper objectMapper, JavaType javaType,
            Map<String, Class<?>> entityTypeToClass) {
        this.objectMapper = objectMapper;
        this.javaType = javaType;
        this.entityTypeToClass = entityTypeToClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T convert(ResponseBody body) throws IOException {
        // Checking if multipart outputs.
        MediaType mediaType = MediaType.fromOkHttpMediaType(body.contentType());
        if (!MediaTypes.APPLICATION_JSON.equalsTypeSubType(mediaType)
                && !MediaTypes.APPLICATION_JSON_NXENTITY.equalsTypeSubType(mediaType)) {
            if (mediaType.type().equals(MediaTypes.MULTIPART_S)) {
                List<Blob> blobs = new ArrayList<>();
                try (InputStream is = body.byteStream()) {
                    MimeMultipart mp = new MimeMultipart(new ByteArrayDataSource(is, mediaType.toString()));
                    int size = mp.getCount();
                    for (int i = 0; i < size; i++) {
                        BodyPart part = mp.getBodyPart(i);
                        // IOUtils.copyToTempFile close the input stream for us
                        blobs.add(new FileBlob(IOUtils.copyToTempFile(part.getInputStream()), part.getFileName(),
                                part.getContentType()));
                    }
                } catch (MessagingException reason) {
                    throw new IOException(reason);
                }
                return (T) new Blobs(blobs);
            }
            // there's post treatment in NuxeoClient on blob
            else if (javaType.getRawClass().equals(StreamBlob.class)) {
                return (T) new StreamBlob(body.byteStream(), null, mediaType.toString());
            }
            // deprecated since 3.1
            else if (javaType.getRawClass().equals(FileBlob.class)) {
                // IOUtils.copyToTempFile close the input stream for us
                File tmpFile = IOUtils.copyToTempFile(body.byteStream());
                return (T) new FileBlob(tmpFile);
            }
            // automation case
            else {
                // for backward compatibility we need to return a FileBlob
                return (T) new FileStreamBlob(body.byteStream());
            }
        }
        // Checking the type of the method clientside
        // When it's Object we look for entity-type in header and then in payload to deduce java type
        // Mechanism used for Operation and Document Adapter
        if (javaType.getRawClass().equals(Object.class)) {
            String entityType = mediaType.nuxeoEntity();
            String bodyString = Responses.bodyToString(body);
            body.close();
            if (entityType == null) {
                // Handle the legacy case when no 'entity-type' header has been set in the response but
                // `entity-type` is written in the json payload
                entityType = bodyString.replaceFirst(".*\"" + ConstantsV1.ENTITY_TYPE + "\":\"([^\"]*)\".*", "$1");
                entityType = StringUtils.trimToNull(entityType);
            }
            Class<?> entityClass = entityTypeToClass.get(entityType);
            // If we can't find an appropriate class to map response just return the plain text
            if (entityClass == null) {
                return (T) bodyString;
            }
            objectMapper.readerFor(Object.class);
            return (T) readJSON(bodyString, entityClass);
        }
        // Delegate other cases to jackson
        try (Reader reader = body.charStream()) {
            ObjectReader objectReader = objectMapper.readerFor(javaType);
            return objectReader.readValue(reader);
        } catch (IOException reason) {
            throw new NuxeoClientException("Error during deserialization of HTTP body", reason);
        }
    }

    public <U> U readJSON(String value, Class<U> javaType) {
        try {
            return objectMapper.readValue(value, javaType);
        } catch (IOException reason) {
            throw new NuxeoClientException("Converter Read Issue.", reason);
        }
    }

}
