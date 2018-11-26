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
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client.objects.comment;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.objects.Document;

/**
 * This API is available since Nuxeo Server 10.3.
 *
 * @since 3.2
 */
public class CommentAdapter extends Document.AbstractAdapter<CommentAdapter> {

    protected static final String NAME = "comment";

    public CommentAdapter(Document document) {
        super(document, NAME);
    }

    protected CommentAdapter(NuxeoClient nuxeoClient, String repositoryName, String documentId) {
        super(nuxeoClient, repositoryName, documentId, NAME);
    }

    public Comment create(Comment comment) {
        // enforce parent id from adapter
        comment.setParentId(documentId);
        return post(comment);
    }

    public Comments list() {
        return get();
    }

    public Comments list(long pageSize, long currentPageIndex) {
        Map<String, Serializable> queryParams = new LinkedHashMap<>();
        queryParams.put("pageSize", pageSize);
        queryParams.put("currentPageIndex", currentPageIndex);
        return get(queryParams);
    }

    public Comment fetch(String commentId) {
        return get(commentId);
    }

    public Comment fetchByEntityId(String entityId) {
        return get("external/" + entityId);
    }

    public Comment update(Comment comment) {
        return update(comment.getId(), comment);
    }

    public Comment update(String commentId, Comment comment) {
        // enforce parent id from adapter
        comment.setParentId(documentId);
        return put(commentId, comment);
    }

    public Comment updateByEntityId(String entityId, Comment comment) {
        // enforce parent id from adapter
        comment.setParentId(documentId);
        return put("external/" + entityId, comment);
    }

    public void remove(String commentId) {
        delete(commentId);
    }

    public void removeByEntityId(String entityId) {
        delete("external/" + entityId);
    }

    public CommentAdapter repliesAdapter(String parentId) {
        return new CommentAdapter(nuxeoClient, repositoryName, parentId);
    }

}
