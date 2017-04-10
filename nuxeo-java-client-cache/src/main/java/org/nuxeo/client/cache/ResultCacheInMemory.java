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
package org.nuxeo.client.cache;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import retrofit2.Response;

/**
 * @since 0.1
 */
public class ResultCacheInMemory implements NuxeoResponseCache {

    protected final Cache<String, Response<?>> cache;

    protected static final int DEFAULT_CONCURRENCY_LEVEL = 10;

    protected static final int DEFAULT_MAXIMUM_SIZE = 1000;

    protected static final int DEFAULT_TIMEOUT = 10;

    public Cache<String, Response<?>> getCache() {
        return cache;
    }

    public ResultCacheInMemory() {
        this(DEFAULT_CONCURRENCY_LEVEL, DEFAULT_MAXIMUM_SIZE, DEFAULT_TIMEOUT, TimeUnit.MINUTES);
    }

    public ResultCacheInMemory(int concurrencyLevel, long maximumSize, long timeout, TimeUnit timeUnit) {
        cache = CacheBuilder.newBuilder()
                            .concurrencyLevel(concurrencyLevel)
                            .maximumSize(maximumSize)
                            .expireAfterWrite(timeout, timeUnit)
                            .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Response<T> getResponse(String key) {
        return (Response<T>) cache.getIfPresent(key);
    }

    @Override
    public void put(String key, Response<?> value) {
        cache.put(key, value);
    }

    @Override
    public long size() {
        return cache.size();
    }

    @Override
    public <T> T getBody(String key) {
        Response<T> response = getResponse(key);
        if (response == null) {
            return null;
        }
        return response.body();
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }

}
