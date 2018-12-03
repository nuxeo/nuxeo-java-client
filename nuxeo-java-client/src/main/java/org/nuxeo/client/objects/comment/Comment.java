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
package org.nuxeo.client.objects.comment;

import java.time.Instant;
import java.util.Set;

import org.nuxeo.client.objects.Entity;
import org.nuxeo.client.objects.EntityTypes;

/**
 * This API is available since Nuxeo Server 10.3.
 *
 * @since 3.2
 */
public class Comment extends Entity {

    protected String id;

    protected String parentId;

    // computed server side
    protected Set<String> ancestorIds; // NOSONAR

    protected String author;

    protected String text;

    protected Instant creationDate;

    protected Instant modificationDate;

    protected String entityId;

    protected String origin;

    protected String entity;

    /**
     * Protected constructor to extend Comment type.
     */
    protected Comment(String entityType) {
        super(entityType);
    }

    public Comment() {
        this(EntityTypes.COMMENT);
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    protected void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Set<String> getAncestorIds() {
        return ancestorIds;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Instant getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Instant modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
