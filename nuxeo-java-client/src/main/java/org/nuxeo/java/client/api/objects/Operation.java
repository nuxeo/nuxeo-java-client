/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
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
package org.nuxeo.java.client.api.objects;

import java.io.IOException;
import java.util.Map;

import com.squareup.okhttp.MediaType;
import org.nuxeo.java.client.api.ConstantsV1;
import org.nuxeo.java.client.api.NuxeoClient;
import org.nuxeo.java.client.api.methods.OperationAPI;
import org.nuxeo.java.client.api.objects.blob.FileBlob;
import org.nuxeo.java.client.internals.spi.NuxeoClientException;
import org.nuxeo.java.client.internals.util.IOUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.squareup.okhttp.ResponseBody;

/**
 * @since 1.0
 */
public class Operation extends NuxeoObject {

    @JsonIgnore
    protected final OperationAPI operationAPI;

    @JsonIgnore
    protected OperationBody body;

    protected final NuxeoClient nuxeoClient;

    protected String operationId;

    public Operation(NuxeoClient nuxeoClient) {
        super(ConstantsV1.ENTITY_TYPE_OPERATION, nuxeoClient);
        operationAPI = nuxeoClient.getRetrofit().create(OperationAPI.class);
        body = new OperationBody();
        this.nuxeoClient = nuxeoClient;
    }

    public Object execute(String operationId, OperationBody body) {
        ResponseBody responseBody = (ResponseBody) getResponse(operationAPI, operationId, body);
        try {
            if(!responseBody.contentType().equals(ConstantsV1.APPLICATION_JSON)){
                return new FileBlob(IOUtils.copyToTempFile(responseBody.byteStream()));
            }
            String response = responseBody.string();
            Object objectResponse = nuxeoClient.getConverterFactory().readJSON(response, Object.class);
            switch ((String) ((Map<String, Object>) objectResponse).get(ConstantsV1.ENTITY_TYPE)) {
            case ConstantsV1.ENTITY_TYPE_DOCUMENT:
                return nuxeoClient.getConverterFactory().readJSON(response, Document.class);
            case ConstantsV1.ENTITY_TYPE_DOCUMENTS:
                return nuxeoClient.getConverterFactory().readJSON(response, Documents.class);
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
