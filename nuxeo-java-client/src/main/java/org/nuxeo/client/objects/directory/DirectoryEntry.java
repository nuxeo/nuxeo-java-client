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

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.client.methods.DirectoryManagerAPI;
import org.nuxeo.client.objects.ConnectableEntity;
import org.nuxeo.client.objects.EntityTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    protected String directoryName;

    protected Map<String, Object> properties = new HashMap<>();

    public DirectoryEntry() {
        super(EntityTypes.DIRECTORY_ENTRY, DirectoryManagerAPI.class);
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
    public String getIdProperty() {
        return getProperty(ID_PROPERTY);
    }

    @JsonIgnore
    public String putIdProperty(String value) {
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
        return getProperty(ORDERING_PROPERTY);
    }

    @JsonIgnore
    public Integer putOrderingProperty(Integer value) {
        return putProperty(ORDERING_PROPERTY, value);
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
        return fetchResponse(api.updateDirectoryEntry(directoryName, getIdProperty(), this));
    }

}
