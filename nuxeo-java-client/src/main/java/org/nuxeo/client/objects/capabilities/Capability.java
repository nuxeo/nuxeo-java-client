/*
 * (C) Copyright 2024 Nuxeo (http://nuxeo.com/) and others.
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
 *     Kevin Leturc <kevin.leturc@hyland.com>
 */
package org.nuxeo.client.objects.capabilities;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * @since 4.0.0
 */
public class Capability implements Serializable {

    protected final Map<String, Object> capabilities;

    public Capability(Map<String, Object> capabilities) {
        this.capabilities = Collections.unmodifiableMap(capabilities);
    }

    public boolean hasCapability(String capabilityName) {
        return capabilities.containsKey(capabilityName);
    }

    public boolean capabilityAsBoolean(String capabilityName) {
        return (boolean) capabilities.get(capabilityName);
    }

    public double capabilityAsDouble(String capabilityName) {
        return (double) capabilities.get(capabilityName);
    }

    public int capabilityAsInt(String capabilityName) {
        return (int) capabilities.get(capabilityName);
    }

    public long capabilityAsLong(String capabilityName) {
        return (long) capabilities.get(capabilityName);
    }

    public String capabilityAsString(String capabilityName) {
        return capabilityAs(capabilityName, String::valueOf);
    }

    public Capability capabilityAsCapability(String capabilityName) {
        return capabilityAs(capabilityName, Capability::new);
    }

    @SuppressWarnings("unchecked")
    public <O, C> C capabilityAs(String capabilityName, Function<O, C> mapper) {
        return mapper.apply((O) capabilities.get(capabilityName));
    }
}
