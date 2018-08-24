package org.nuxeo.client.objects.annotation;

import static java.util.Collections.singletonMap;
import static org.nuxeo.client.objects.Document.DEFAULT_FILE_CONTENT;

import org.nuxeo.client.objects.Document;

/**
 * This API is available since Nuxeo Server 10.3.
 *
 * @since 3.2
 */
public class AnnotationAdapter extends Document.Adapter {

    public AnnotationAdapter(Document document) {
        super(document, "annotation");
    }

    public Annotation create(Annotation annotation) {
        // enforce document id from adapter
        annotation.setDocumentId(documentId);
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

    public Annotation update(Annotation annotation) {
        // enforce document id from adapter
        annotation.setDocumentId(documentId);
        return put(annotation);
    }

    public void remove(String annotationId) {
        delete(annotationId);
    }

}
