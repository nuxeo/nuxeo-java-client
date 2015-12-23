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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.java.client.api.objects.Document;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * @since 1.0
 */
public class AutomationMarshaller implements NuxeoMarshaller<Object> {

    @Override
    public Class<Object> getJavaType() {
        return Object.class;
    }

    @Override
    public Object read(JsonParser jp) throws IOException {
        String json = jp.readValueAsTree().toString();
return null;
    }

    @Override
    public void write(JsonGenerator jg, Object value) throws IOException {
        throw new UnsupportedOperationException();
    }

}
