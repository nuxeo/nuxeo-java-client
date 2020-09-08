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

import static java.util.Collections.singletonMap;
import static org.nuxeo.client.objects.Document.DEFAULT_FILE_CONTENT;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.client.NuxeoVersion;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.ProxyRetrofitQueryMap;

/**
 * This API is available since Nuxeo Server 10.3.
 *
 * @since 3.2
 */
public class AnnotationAdapter extends Document.AbstractAdapter<AnnotationAdapter> {

    protected static final int REPLIES_BATCH_SIZE = 50;

    public AnnotationAdapter(Document document) {
        super(document, "annotation");
    }

    public Annotation create(Annotation annotation) {
        // enforce parent id from adapter
        annotation.setParentId(documentId);
        return post(annotation);
    }

    /**
     * Fetches annotations for {@link Document#DEFAULT_FILE_CONTENT} blob.
     */
    public Annotations list() {
        return list(DEFAULT_FILE_CONTENT);
    }

    public Annotations list(String xpath) {
        return get(singletonMap("xpath", xpath));
    }

    public Annotation fetch(String annotationId) {
        return get(annotationId);
    }

    public Annotation fetchByEntityId(String entityId) {
        return get("external/" + entityId);
    }

    public Comments fetchComments(List<String> annotationIds) {
        NuxeoVersion serverVersion = nuxeoClient.getServerVersion();
        if ((serverVersion.isGreaterThan(NuxeoVersion.LTS_10_10.hotfix(32))
                && !serverVersion.isGreaterThan(NuxeoVersion.parse("11.1")))
                || serverVersion.isGreaterThan(NuxeoVersion.parse("11.3.37"))) {
            return post("comments", annotationIds);
        }
        // do a loop on annotationIds to avoid the URI too long error
        Comments comments = new Comments();
        for (int i = 0; i < (annotationIds.size() - 1) / REPLIES_BATCH_SIZE + 1; i++) {
            List<String> ids = annotationIds.subList(i * REPLIES_BATCH_SIZE,
                    Math.min((i + 1) * REPLIES_BATCH_SIZE, annotationIds.size()));
            Comments c = get("comments",
                    new ProxyRetrofitQueryMap(singletonMap("annotationIds", new ArrayList<>(ids))));
            comments.getEntries().addAll(c.getEntries());
        }
        return comments;
    }

    public Annotation update(Annotation annotation) {
        return update(annotation.getId(), annotation);
    }

    public Annotation update(String annotationId, Annotation annotation) {
        // enforce parent id from adapter
        annotation.setParentId(documentId);
        return put(annotationId, annotation);
    }

    public Annotation updateByEntityId(String entityId, Annotation annotation) {
        // enforce parent id from adapter
        annotation.setParentId(documentId);
        return put("external/" + entityId, annotation);
    }

    public void remove(String annotationId) {
        delete(annotationId);
    }

    public void removeByEntityId(String entityId) {
        delete("external/" + entityId);
    }

    public CommentAdapter repliesAdapter(String commentId) {
        return new CommentAdapter(nuxeoClient, repositoryName, commentId);
    }

}
