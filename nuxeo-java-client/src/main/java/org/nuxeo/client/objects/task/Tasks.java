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
package org.nuxeo.client.objects.task;

import java.util.List;

import org.nuxeo.client.objects.Entities;
import org.nuxeo.client.objects.EntityTypes;

/**
 * @since 1.0
 */
public class Tasks extends Entities<Task> {

    public Tasks() {
        super(EntityTypes.TASKS);
    }

    public Tasks(List<? extends Task> entries) {
        super(EntityTypes.TASKS, entries);
    }

    /**
     * @deprecated since 3.2, use {@link #getEntries()} instead
     */
    @Deprecated
    public List<Task> getTasks() {
        return getEntries();
    }

    /**
     * @deprecated since 3.2, use {@link #getEntry(int)} instead
     */
    @Deprecated
    public Task get(int id) {
        return getEntry(id);
    }

}
