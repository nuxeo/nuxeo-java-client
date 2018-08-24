/*
 * (C) Copyright 2018 Nuxeo (http://nuxeo.com/) and others.
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
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client.objects.annotation;

import org.nuxeo.client.objects.Entity;
import org.nuxeo.client.objects.EntityTypes;

/**
 * This API is available since Nuxeo Server 10.2.
 *
 * @since 3.1
 */
public class Annotation extends Entity {

    protected String id;

    protected String documentId;

    protected String xpath;

    protected String entity;

    /**
     * For internal marshalling purpose.
     */
    protected Annotation() {
        super(EntityTypes.ANNOTATION);
    }

    public Annotation(String id, String xpath) {
        this();
        this.id = id;
        this.xpath = xpath;
    }

    public String getId() {
        return id;
    }

    public String getDocumentId() {
        return documentId;
    }

    protected void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getXPath() {
        return xpath;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

}
