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
package org.nuxeo.client.objects.operation;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 0.1
 */
public class DocRefs {

    protected final List<DocRef> docs = new ArrayList<>();

    public void addDoc(DocRef doc) {
        docs.add(doc);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("");
        int size = docs.size();
        if (size == 0) {
            return buf.toString();
        }
        buf.append(docs.get(0).doc);
        for (int i = 1; i < size; i++) {
            buf.append(",").append(docs.get(i).doc);
        }
        return buf.toString();
    }

}
