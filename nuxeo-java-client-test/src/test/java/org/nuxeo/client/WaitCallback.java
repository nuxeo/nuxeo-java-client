/*
 * (C) Copyright 2017 Nuxeo SA (http://nuxeo.com/) and others.
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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.with;

import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @since 3.0.0
 */
public class WaitCallback<T> implements Callback<T> {

    private final AtomicBoolean hasBeenCalled = new AtomicBoolean(false);

    private volatile T body;

    private volatile Exception exception;

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        hasBeenCalled.set(true);
        body = response.body();
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        hasBeenCalled.set(true);
        exception = new Exception(t);
    }

    public T waitForResponse() throws Exception {
        with().pollInSameThread().await().atLeast(100, MILLISECONDS).and().atMost(20, SECONDS).untilTrue(hasBeenCalled);
        if (exception != null) {
            throw exception;
        }
        return body;
    }

}
