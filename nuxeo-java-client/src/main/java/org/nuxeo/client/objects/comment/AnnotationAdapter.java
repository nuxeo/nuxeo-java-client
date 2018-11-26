package org.nuxeo.client.objects.comment;

import static java.util.Collections.singletonMap;
import static org.nuxeo.client.objects.Document.DEFAULT_FILE_CONTENT;

import org.nuxeo.client.objects.Document;

/**
 * This API is available since Nuxeo Server 10.3.
 *
 * @since 3.2
 */
public class AnnotationAdapter extends Document.AbstractAdapter<AnnotationAdapter> {

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
