/*
 * (C) Copyright 2017 Nuxeo (http://nuxeo.com/) and others.
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
package org.nuxeo.client.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.nuxeo.client.HttpHeaders;

import com.fasterxml.jackson.annotation.JsonIgnore;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;

/**
 * @param <B> The type of object extending this one.
 * @since 3.0
 */
public class AbstractBase<B extends AbstractBase<B>> {

    @JsonIgnore
    protected OkHttpClient.Builder okhttpBuilder;

    @JsonIgnore
    protected Retrofit.Builder retrofitBuilder;

    @JsonIgnore
    protected Map<String, Interceptor> headerInterceptors;

    @JsonIgnore
    protected Map<String, List<String>> headerValues;

    @JsonIgnore
    protected Retrofit retrofit;

    public AbstractBase() {
        // okhttp builder
        okhttpBuilder = new OkHttpClient.Builder();
        // retrofit builder
        retrofitBuilder = new Retrofit.Builder();
        // header interceptors
        headerInterceptors = new HashMap<>();
        // header values
        headerValues = new HashMap<>();
    }

    protected AbstractBase(AbstractBase<?> base) {
        replaceWith(base);
    }

    /**
     * Sets retry on connection failure or not.
     */
    public B retryOnConnectionFailure(boolean enable) {
        okhttpBuilder.retryOnConnectionFailure(enable);
        buildRetrofit();
        return (B) this;
    }

    /**
     * Sets the given timeout to connect, read and write timeout settings of client. The timeout unit is seconds.
     */
    @SuppressWarnings("unchecked")
    public B timeout(long timeout) {
        okhttpBuilder.connectTimeout(timeout, TimeUnit.SECONDS);
        okhttpBuilder.readTimeout(timeout, TimeUnit.SECONDS);
        okhttpBuilder.writeTimeout(timeout, TimeUnit.SECONDS);
        buildRetrofit();
        return (B) this;
    }

    /**
     * The timeout unit is seconds.
     */
    @SuppressWarnings("unchecked")
    public B connectTimeout(long connectTimeout) {
        okhttpBuilder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
        buildRetrofit();
        return (B) this;
    }

    /**
     * The timeout unit is seconds.
     */
    @SuppressWarnings("unchecked")
    public B readTimeout(long readTimeout) {
        okhttpBuilder.readTimeout(readTimeout, TimeUnit.SECONDS);
        buildRetrofit();
        return (B) this;
    }

    /**
     * The timeout unit is seconds.
     */
    @SuppressWarnings("unchecked")
    public B writeTimeout(long writeTimeout) {
        okhttpBuilder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
        buildRetrofit();
        return (B) this;
    }

    /**
     * Replaces the header value with input one.
     *
     * @since 3.2
     */
    public B header(String header, boolean value) {
        return header(false, header, Boolean.toString(value));
    }

    /**
     * Replaces the header value with the input one separated by ','.
     *
     * @since 3.2
     */
    public B header(String header, int value, int... values) {
        return header(false, header, value, values);
    }

    /**
     * Replaces or appends the header value with the input one separated by ','.
     *
     * @since 3.2
     */
    public B header(boolean append, String header, int value, int... values) {
        return header(append, header, Integer.toString(value),
                IntStream.of(values).mapToObj(Integer::toString).toArray(String[]::new));
    }

    /**
     * Replaces the header value with the input one separated by ','.
     *
     * @since 3.2
     */
    public B header(String header, Serializable value, Serializable... values) {
        return header(false, header, value, values);
    }

    /**
     * Replaces or appends the header value with the input one separated by ','.
     *
     * @since 3.2
     */
    public B header(boolean append, String header, Serializable value, Serializable... values) {
        return header(append, header, value.toString(),
                Stream.of(values).map(Serializable::toString).toArray(String[]::new));
    }

    /**
     * Replaces the header value with the input one separated by ','.
     */
    public B header(String header, String value, String... values) {
        return header(false, header, value, values);
    }

    /**
     * Replaces or appends the input header value with the input one separated by ','.
     */
    @SuppressWarnings("unchecked")
    public B header(boolean append, String header, String value, String... values) {
        // compute the header values and put it into the map (depending on append strategy)
        List<String> lValues = headerValues.compute(header, (k, vOld) -> {
            List<String> vNew = new ArrayList<>(1 + values.length);
            vNew.add(value);
            vNew.addAll(Arrays.asList(values));
            if (append && vOld != null) {
                vNew.addAll(vOld);
            }
            return vNew;
        });
        // remove previous header
        Interceptor previousInterceptor = headerInterceptors.remove(header);
        if (previousInterceptor != null) {
            okhttpBuilder.interceptors().remove(previousInterceptor);
        }
        // compute the final header value and create a okhttp interceptor
        String headerValue = String.join(",", lValues);
        Interceptor interceptor = chain -> {
            Request request = chain.request();
            request = request.newBuilder().addHeader(header, headerValue).build();
            return chain.proceed(request);
        };
        headerInterceptors.put(header, interceptor);
        okhttpBuilder.interceptors().add(interceptor);
        buildRetrofit();
        return (B) this;
    }

    public B transactionTimeout(long timeout) {
        return header(HttpHeaders.NUXEO_TX_TIMEOUT, String.valueOf(timeout));
    }

    public B enrichers(boolean append, String type, String enricher, String... enrichers) {
        return header(append, "enrichers-" + type, enricher, enrichers);
    }

    /**
     * Replaces the current enrichers associate to the input type by the input one.
     */
    public B enrichers(String type, String enricher, String... enrichers) {
        return enrichers(false, type, enricher, enrichers);
    }

    /**
     * Replaces the current enrichers associate to document type by the input one.
     */
    public B enrichersForDocument(String enricher, String... enrichers) {
        return enrichers("document", enricher, enrichers);
    }

    public B fetchProperties(boolean append, String fetch, String fetchProperty, String... fetchProperties) {
        return header(append, "fetch-" + fetch, fetchProperty, fetchProperties);
    }

    /**
     * Replaces the current fetch properties associate to the input type by the input one.
     */
    public B fetchProperties(String type, String fetchProperty, String... fetchProperties) {
        return fetchProperties(false, type, fetchProperty, fetchProperties);
    }

    /**
     * Replaces the current fetch properties associate to document type by the input one.
     */
    public B fetchPropertiesForDocument(String fetchProperty, String... fetchProperties) {
        return fetchProperties("document", fetchProperty, fetchProperties);
    }

    /**
     * Replaces the current fetch properties associate to group type by the input one. Possible values are:
     * <ul>
     * <li>memberUsers</li>
     * <li>memberGroups</li>
     * <li>parentGroups</li>
     * </ul>
     */
    public B fetchPropertiesForGroup(String fetchProperty, String... fetchProperties) {
        return fetchProperties("group", fetchProperty, fetchProperties);
    }

    /**
     * Sets the depth. Possible values are: `root`, `children` and `max`.
     *
     * @see org.nuxeo.ecm.core.io.registry.context.DepthValues
     */
    @SuppressWarnings("JavadocReference")
    public B depth(String value) {
        return header(HttpHeaders.DEPTH, value);
    }

    public B version(String value) {
        return header(HttpHeaders.X_VERSIONING_OPTION, value);
    }

    public B schemas(boolean append, String property, String... properties) {
        return header(append, HttpHeaders.X_PROPERTIES, property, properties);
    }

    /**
     * Replaces the current schemas by the input one.
     */
    public B schemas(String property, String... properties) {
        return schemas(false, property, properties);
    }

    /**
     * Re-build the retrofit context.
     * <p />
     * You may want to override this method if you want to do more with the built retrofit, for instance create a new
     * retrofit API.
     */
    protected void buildRetrofit() {
        OkHttpClient okhttp = okhttpBuilder.build();
        retrofit = retrofitBuilder.callFactory(okhttp).build();
    }

    /**
     * Replaces the input {@link AbstractBase} into this one. This method will create a new retrofit object in order to
     * correctly isolate this configuration.
     * <p />
     * DON'T REMOVE FINAL KEYWORD, THIS METHOD IS USED IN A CONSTRUCTOR.
     *
     * @param base the base to replace
     */
    protected final void replaceWith(AbstractBase<?> base) {
        // in order to have a retrofit instance and correctly isolate this configuration from the copied one, we build
        // retrofit
        OkHttpClient okhttp = base.okhttpBuilder.build();
        retrofit = base.retrofitBuilder.callFactory(okhttp).build();
        // get new builders
        // okhttp builder
        okhttpBuilder = okhttp.newBuilder();
        // retrofit builder
        retrofitBuilder = retrofit.newBuilder();
        // copy header interceptors
        headerInterceptors = new HashMap<>();
        headerInterceptors.putAll(base.headerInterceptors);
        // copy header values
        headerValues = new HashMap<>();
        headerValues.putAll(base.headerValues);
    }

}
