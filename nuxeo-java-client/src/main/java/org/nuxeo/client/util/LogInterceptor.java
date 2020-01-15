/*
 * (C) Copyright 2018-2019 Nuxeo SA (http://nuxeo.com/).
 *   This is unpublished proprietary source code of Nuxeo SA. All rights reserved.
 *   Notice of copyright on this source code does not indicate publication.
 *
 *   Contributors:
 *       Nuxeo
 */
package org.nuxeo.client.util;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @since 0.1
 */
public class LogInterceptor implements Interceptor {

    private static final Logger log = LogManager.getLogger(LogInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody requestBody = request.body();
        Connection connection = chain.connection();
        boolean hasRequestBody = requestBody != null;
        String requestStartMessage = "--> " + request.method() + ' ' + request.url()
                + (connection != null ? " " + connection.protocol() : "");
        if (hasRequestBody) {
            requestStartMessage = requestStartMessage + " (" + requestBody.contentLength() + "-byte body)";
        }

        log.debug(requestStartMessage);
        if (hasRequestBody) {
            if (requestBody.contentType() != null) {
                log.debug("Content-Type: {}", requestBody.contentType());
            }

            if (requestBody.contentLength() != -1L) {
                log.debug("Content-Length: {}", requestBody.contentLength());
            }
        }
        Headers headers = request.headers();
        int i = 0;
        for (int count = headers.size(); i < count; ++i) {
            String name = headers.name(i);
            if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                logHeader(headers, i);
            }
        }
        if (hasRequestBody) {
            log.debug("--> END {} (binary {}-byte body omitted)", request.method(), requestBody.contentLength());
        } else {
            log.debug("--> END {}", request.method());
        }
        Response response = chain.proceed(request);
        log.debug("--> RESPONSE {}", response.toString());
        return response;
    }

    private void logHeader(Headers headers, int i) {
        log.debug("{}:{}", headers.name(i), headers.value(i));
    }
}
