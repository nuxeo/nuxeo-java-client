/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
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
 *         Vladimir Pasquier <vpasquier@nuxeo.com>
 */
package org.nuxeo.client.api.objects.workflow;

import java.util.List;
import java.util.Map;

import org.nuxeo.client.api.ConstantsV1;
import org.nuxeo.client.api.objects.NuxeoEntity;

/**
 * @since 0.1
 */
public class Graph extends NuxeoEntity {

    public Graph() {
        super(ConstantsV1.ENTITY_TYPE_GRAPH);
    }

    protected List<Map<String, Object>> nodes;

    protected List<Map<String, Object>> transitions;

    public List<Map<String, Object>> getNodes() {
        return nodes;
    }

    public List<Map<String, Object>> getTransitions() {
        return transitions;
    }
}
