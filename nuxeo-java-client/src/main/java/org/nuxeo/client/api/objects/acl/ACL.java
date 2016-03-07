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
package org.nuxeo.client.api.objects.acl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 0.1
 */
public class ACL {

    public static final String LOCAL_ACL = "local";

    public static final String INHERITED_ACL = "inherited";

    protected String name;

    @JsonProperty("ace")
    protected List<ACE> aces;

    public String getName() {
        return name;
    }

    public List<ACE> getAces() {
        return aces;
    }

    public void setAces(List<ACE> aces) {
        this.aces = aces;
    }

    public void setName(String name) {
        this.name = name;
    }
}