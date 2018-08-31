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
public class CommentAdapter extends Document.Adapter {

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
