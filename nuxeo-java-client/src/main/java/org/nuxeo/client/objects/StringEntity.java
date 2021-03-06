/*
 * (C) Copyright 2020 Nuxeo (http://nuxeo.com/) and others.
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
 *     jcarsique
 *
 */

package org.nuxeo.client.objects;

/**
 * @since 3.9.0
 */
public class StringEntity extends Entity {

    protected String value;

    public StringEntity() {
        super(EntityTypes.STRING);
    }

    public String getValue() {
        return value;
    }

    protected void setValue(String value) {
        this.value = value;
    }
}
