/*
 * (C) Copyright 2017 Nuxeo SA (http://nuxeo.com/) and others.
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
package org.nuxeo.client.operations;

import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;

import net.sf.json.JSONObject;

/**
 * Custom operation returning a custom json blob
 */
@Operation(id = CustomOperationJSONBlob.ID, category = "Document", label = "CustomOperationJSONBlob")
public class CustomOperationJSONBlob {

    public static final String ID = "CustomOperationJSONBlob";

    @OperationMethod
    public Blob run() {

        JSONObject attributes = new JSONObject();
        attributes.put("userId", "1");
        attributes.put("token", "token");

        return Blobs.createBlob(attributes.toString(), "application/json");
    }
}