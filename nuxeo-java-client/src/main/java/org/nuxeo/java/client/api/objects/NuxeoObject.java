package org.nuxeo.java.client.api.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 1.0
 */
public abstract class NuxeoObject {

    @JsonProperty("entity-type")
    protected final String entityType;

    protected NuxeoObject(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityType() {
        return entityType;
    }

}
