/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *          Nuxeo
 */
package org.nuxeo.java.client.api.marshaller;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

/**
 * Plugs in automation client new input/output marshalling logic.
 */
public interface NuxeoMarshaller<T> {

    /**
     * The marshalled java type
     */
    Class<T> getJavaType();

    /**
     * Builds and returns a POJO from the JSON object
     */
    T read(JsonParser jp) throws IOException;

    /**
     * Writes the POJO object to the JsonGenerator
     */
    void write(JsonGenerator jg, Object value) throws IOException;

}
