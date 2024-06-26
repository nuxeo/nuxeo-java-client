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
 *     Vladimir Pasquier <vpasquier@nuxeo.com>
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client.objects.directory;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.client.methods.DirectoryManagerAPI;
import org.nuxeo.client.objects.ConnectableEntity;
import org.nuxeo.client.objects.EntityTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @since 0.1
 */
public class DirectoryEntry extends ConnectableEntity<DirectoryManagerAPI, DirectoryEntry> {

    /**
     * @since 3.0
     */
    public static final String ID_PROPERTY = "id";

    /**
     * @since 3.0
     */
    public static final String LABEL_PROPERTY = "label";

    /**
     * @since 3.0
     */
    public static final String ORDERING_PROPERTY = "ordering";

    /**
     * @since 3.0
     */
    public static final String OBSOLETE_PROPERTY = "obsolete";

    /**
     * @since Nuxeo 9.3 - Nuxeo LTS 2017
     */
    protected String id;

    protected String directoryName;

    protected Map<String, Object> properties = new HashMap<>();

    /**
     * Regular way to instantiate a {@link DirectoryEntry} in order to create it.
     */
    public DirectoryEntry() {
        super(EntityTypes.DIRECTORY_ENTRY, DirectoryManagerAPI.class);
    }

    /**
     * Regular way to instantiate a {@link DirectoryEntry} in order to update it.
     *
     * @since 3.12.0
     */
    public DirectoryEntry(String id) {
        this();
        this.id = id;
    }

    /**
     * Since NXP-22739, id is serialized as {@link String} beside properties to face type issue.
     *
     * @return the id field if present, otherwise try to convert it to {@link String} from {@link #getIdProperty()}
     */
    @JsonInclude(Include.NON_NULL)
    public String getId() {
        if (isNotEmpty(id)) {
            return id;
        }
        Object idProperty = getIdProperty();
        return idProperty == null ? null : String.valueOf(idProperty);
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, ?> properties) {
        this.properties.clear();
        if (properties != null) {
            this.properties.putAll(properties);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key) {
        return properties == null ? null : (T) properties.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T putProperty(String key, T value) {
        return (T) properties.put(key, value);
    }

    @JsonIgnore
    public <T> T getIdProperty() {
        return getProperty(ID_PROPERTY);
    }

    @JsonIgnore
    public <T> T putIdProperty(T value) {
        return putProperty(ID_PROPERTY, value);
    }

    @JsonIgnore
    public String getLabelProperty() {
        return getProperty(LABEL_PROPERTY);
    }

    @JsonIgnore
    public String putLabelProperty(String value) {
        return putProperty(LABEL_PROPERTY, value);
    }

    @JsonIgnore
    public Long getOrderingProperty() {
        Number value = getProperty(ORDERING_PROPERTY);
        return value == null ? null : value.longValue();
    }

    /**
     * @since 3.12
     */
    @JsonIgnore
    public Long putOrderingProperty(Long value) {
        Number result = putProperty(ORDERING_PROPERTY, value);
        return result == null ? null : result.longValue();
    }

    @JsonIgnore
    public Integer getObsoleteProperty() {
        return getProperty(OBSOLETE_PROPERTY);
    }

    @JsonIgnore
    public Integer putObsoleteProperty(Integer value) {
        return putProperty(OBSOLETE_PROPERTY, value);
    }

    public DirectoryEntry update() {
        return fetchResponse(api.updateDirectoryEntry(directoryName, getId(), this));
    }

    /**
     * @since 3.0
     */
    public void delete() {
        fetchResponse(api.deleteDirectoryEntry(directoryName, getId()));
    }

}
