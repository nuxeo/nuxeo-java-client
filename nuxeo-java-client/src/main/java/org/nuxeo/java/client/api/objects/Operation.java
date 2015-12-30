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
package org.nuxeo.java.client.api.objects;

import java.io.IOException;
import java.util.Map;

import org.nuxeo.java.client.api.ConstantsV1;
import org.nuxeo.java.client.api.NuxeoClient;
import org.nuxeo.java.client.api.methods.OperationAPI;
import org.nuxeo.java.client.api.objects.blob.FileBlob;
import org.nuxeo.java.client.api.objects.operation.OperationBody;
import org.nuxeo.java.client.internals.spi.NuxeoClientException;
import org.nuxeo.java.client.internals.util.IOUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;

/**
 * @since 1.0
 */
public class Operation extends NuxeoEntity {

    @JsonIgnore
    protected OperationBody body;

    protected String operationId;

    public Operation(NuxeoClient nuxeoClient) {
        super(ConstantsV1.ENTITY_TYPE_OPERATION, nuxeoClient, OperationAPI.class);
        body = new OperationBody();
    }

    public Object execute(String operationId, OperationBody body) {
        ResponseBody responseBody = (ResponseBody) getResponse(operationId, body);
        try {
            MediaType mediaType = responseBody.contentType();
            if (!mediaType.equals(ConstantsV1.APPLICATION_JSON)
                    && !mediaType.equals(ConstantsV1.APPLICATION_JSON_NXENTITY)) {
                return new FileBlob(IOUtils.copyToTempFile(responseBody.byteStream()));
            }
            String response = responseBody.string();
            Object objectResponse = nuxeoClient.getConverterFactory().readJSON(response, Object.class);
            switch ((String) ((Map<String, Object>) objectResponse).get(ConstantsV1.ENTITY_TYPE)) {
            case ConstantsV1.ENTITY_TYPE_DOCUMENT:
                return nuxeoClient.getConverterFactory().readJSON(response, Document.class);
            case ConstantsV1.ENTITY_TYPE_DOCUMENTS:
                return nuxeoClient.getConverterFactory().readJSON(response, Documents.class);
            case ConstantsV1.ENTITY_TYPE_RECORDSET:
                return nuxeoClient.getConverterFactory().readJSON(response, RecordSet.class);
                // TODO:JAVACLIENT-31
            case ConstantsV1.ENTITY_TYPE_BLOB:
                return nuxeoClient.getConverterFactory().readJSON(response, Blob.class);
            case ConstantsV1.ENTITY_TYPE_BLOBS:
                return nuxeoClient.getConverterFactory().readJSON(response, Blob.class);
            default:
                return objectResponse;
            }
        } catch (IOException reason) {
            throw new NuxeoClientException("Error while unmarshalling Automation response", reason);
        }
    }

    public Object execute(String operationId) {
        return execute(operationId, this.body);
    }

    public Object execute() {
        return execute(this.operationId, this.body);
    }

    public Operation newRequest(String operationId) {
        this.operationId = operationId;
        return this;
    }

    public Operation input(Object input) {
        body.setInput(input);
        return this;
    }

    public Operation param(String key, Object parameter) {
        body.getParameters().put(key, parameter);
        return this;
    }

    public Operation ctx(String key, Object contextValue) {
        body.getContext().put(key, contextValue);
        return this;
    }

    public Operation parameters(Map<String, Object> parameters) {
        body.setParameters(parameters);
        return this;
    }

    public Operation context(Map<String, Object> context) {
        body.setContext(context);
        return this;
    }

}
