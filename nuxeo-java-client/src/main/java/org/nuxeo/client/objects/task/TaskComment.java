package org.nuxeo.client.objects.task;

import java.io.Serializable;
import java.time.Instant;

public class TaskComment implements Serializable {

    protected String author;

    protected String text;

    protected Instant date;

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public Instant getDate() {
        return date;
    }
}
