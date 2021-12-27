/*
 * (C) Copyright 2021 Nuxeo (http://nuxeo.com/) and others.
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
package org.nuxeo.client;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class LogTestOnServerRule extends TestWatcher {

    protected final NuxeoClient client;

    public LogTestOnServerRule(NuxeoClient client) {
        this.client = client;
    }

    @Override
    protected void starting(Description description) {
        client.operation("Log")
              .param("message", "Starting test: " + description.getDisplayName())
              .param("level", "warn")
              .execute();
    }

    @Override
    protected void finished(Description description) {
        client.operation("Log")
                .param("message", "Finished test: " + description.getDisplayName())
                .param("level", "warn")
                .execute();
    }
}
