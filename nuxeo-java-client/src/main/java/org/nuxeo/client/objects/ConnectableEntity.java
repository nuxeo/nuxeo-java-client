/*
 * (C) Copyright 2017 Nuxeo (http://nuxeo.com/) and others.
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
package org.nuxeo.client.objects;

import java.util.Objects;

import org.nuxeo.client.NuxeoClient;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @param <A> The api interface type.
 * @since 3.0
 */
public class ConnectableEntity<A> extends AbstractConnectable<A> {

    @JsonProperty("entity-type")
    protected final String entityType;

    /**
     * Minimal constructor to use benefit of injection mechanism.
     */
    protected ConnectableEntity(String entityType, Class<A> apiClass) {
        super(apiClass);
        this.entityType = Objects.requireNonNull(entityType, "'entity-type' must be provider");
    }

    /**
     * The constructor to use.
     */
    protected ConnectableEntity(String entityType, Class<A> apiClass, NuxeoClient nuxeoClient) {
        super(apiClass, nuxeoClient);
        this.entityType = Objects.requireNonNull(entityType, "'entity-type' must be provider");
    }

    public String getEntityType() {
        return entityType;
    }

}
