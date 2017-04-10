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
package org.nuxeo.client.objects.operation;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.client.marshaller.OperationInputSerializer;
import org.nuxeo.client.objects.blob.Blob;
import org.nuxeo.client.objects.blob.Blobs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @since 0.1
 */
public class OperationBody {

    @JsonProperty("params")
    protected Map<String, Object> parameters;

    @JsonProperty("context")
    protected Map<String, Object> context;

    @JsonIgnore
    protected Object input;

    public OperationBody() {
        this.parameters = new HashMap<>();
        this.context = new HashMap<>();
        this.input = null;
    }

    /**
     * @return input needed to execute the request (ie: remove blob from input serialization)
     */
    @JsonProperty("input")
    @JsonSerialize(using = OperationInputSerializer.class)
    public Object input() {
        if (input instanceof Blob || input instanceof Blobs) {
            return null;
        }
        return input;
    }

    @JsonIgnore
    public Object getInput() {
        return input;
    }

    public void setInput(Object input) {
        this.input = input;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

}
