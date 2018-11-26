/*
 * (C) Copyright 2018 Nuxeo (http://nuxeo.com/) and others.
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

import java.util.List;

import org.nuxeo.client.objects.Entities;
import org.nuxeo.client.objects.EntityTypes;

/**
 * This API is available since Nuxeo Server 10.2.
 *
 * @since 3.1
 */
public class Annotations extends Entities<Annotation> {

    public Annotations() {
        super(EntityTypes.ANNOTATIONS);
    }

    /**
     * @deprecated since 3.2, use {@link #getEntries()} instead.
     */
    @Deprecated
    public List<Annotation> getAnnotations() {
        return getEntries();
    }

    /**
     * @deprecated since 3.2, use {@link #getEntry(int)} ()} instead.
     */
    @Deprecated
    public Annotation get(int id) {
        return getEntry(id);
    }

}
