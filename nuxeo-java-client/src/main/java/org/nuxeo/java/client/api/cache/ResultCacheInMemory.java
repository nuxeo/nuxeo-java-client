/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
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
package org.nuxeo.java.client.api.cache;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.nuxeo.java.client.internals.spi.NuxeoClientException;

import retrofit.Response;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @since 1.0
 */
public class ResultCacheInMemory implements NuxeoResponseCache {

    protected final Cache<String, Object> cache;

    protected static final Integer CACHE_CONCURRENCY_LEVEL = 10;

    protected static final Integer CACHE_MAXIMUM_SIZE = 1000;

    protected static final Integer CACHE_TIMEOUT = 10;

    public com.google.common.cache.Cache<String, Object> getCache() {
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

}
