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
package org.nuxeo.client.api.cache;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.nuxeo.client.internals.spi.NuxeoClientException;

import retrofit2.Response;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @since 0.1
 */
public class ResultCacheInMemory implements NuxeoResponseCache {

    protected final Cache<String, Object> cache;

    protected static final Integer CACHE_CONCURRENCY_LEVEL = 10;

    protected static final Integer CACHE_MAXIMUM_SIZE = 1000;

    protected static final Integer CACHE_TIMEOUT = 10;

    public Cache<String, Object> getCache() {
        return cache;
    }

    public ResultCacheInMemory() {
        cache = CacheBuilder.newBuilder()
                            .concurrencyLevel(CACHE_CONCURRENCY_LEVEL)
                            .maximumSize(CACHE_MAXIMUM_SIZE)
                            .expireAfterWrite(CACHE_TIMEOUT, TimeUnit.MINUTES)
                            .build();
    }

    public ResultCacheInMemory(int cache_concurrency_level, long cache_maximum_size, long cache_timeout,
            TimeUnit time_unit) {
        cache = CacheBuilder.newBuilder()
                            .concurrencyLevel(cache_concurrency_level)
                            .maximumSize(cache_maximum_size)
                            .expireAfterWrite(cache_timeout, time_unit)
                            .build();
    }

    @Override
    public Object getResponse(Object key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void put(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public int size() {
        return cache.asMap().size();
    }

    @Override
    public Object getRaw(Object key) {
        Object result = cache.getIfPresent(key);
        if (result == null) {
            return null;
        }
        try {
            return ((Response) result).raw().body().string();
        } catch (IOException reason) {
            throw new NuxeoClientException(reason);
        }
    }

    @Override
    public Object getBody(Object key) {
        Object result = cache.getIfPresent(key);
        if (result == null) {
            return null;
        }
        return ((Response) result).body();
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }
}
