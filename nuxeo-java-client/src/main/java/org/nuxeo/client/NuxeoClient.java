/*
 * (C) Copyright 2016-2018 Nuxeo (http://nuxeo.com/) and others.
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
package org.nuxeo.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.client.cache.NuxeoResponseCache;
import org.nuxeo.client.marshaller.NuxeoConverterFactory;
import org.nuxeo.client.objects.AbstractBase;
import org.nuxeo.client.objects.Connectable;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.Documents;
import org.nuxeo.client.objects.EntityTypes;
import org.nuxeo.client.objects.Operation;
import org.nuxeo.client.objects.RecordSet;
import org.nuxeo.client.objects.Repository;
import org.nuxeo.client.objects.blob.Blob;
import org.nuxeo.client.objects.blob.Blobs;
import org.nuxeo.client.objects.blob.FileBlob;
import org.nuxeo.client.objects.blob.FileStreamBlob;
import org.nuxeo.client.objects.blob.StreamBlob;
import org.nuxeo.client.objects.comment.Annotation;
import org.nuxeo.client.objects.comment.Annotations;
import org.nuxeo.client.objects.comment.Comment;
import org.nuxeo.client.objects.comment.Comments;
import org.nuxeo.client.objects.config.ConfigManager;
import org.nuxeo.client.objects.directory.DirectoryManager;
import org.nuxeo.client.objects.task.Task;
import org.nuxeo.client.objects.task.TaskManager;
import org.nuxeo.client.objects.task.Tasks;
import org.nuxeo.client.objects.upload.BatchUploadManager;
import org.nuxeo.client.objects.user.User;
import org.nuxeo.client.objects.user.UserManager;
import org.nuxeo.client.spi.NuxeoClientException;
import org.nuxeo.client.spi.NuxeoClientRemoteException;
import org.nuxeo.client.spi.auth.BasicAuthInterceptor;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Version;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * @since 0.1
 */
public class NuxeoClient extends AbstractBase<NuxeoClient> {

    public static final Pattern CMIS_PRODUCT_VERSION_PATTERN = Pattern.compile("\"productVersion\":\"(.*?)\"");

    protected final NuxeoConverterFactory converterFactory;

    protected NuxeoResponseCache nuxeoCache;

    protected User currentUser;

    protected NuxeoVersion serverVersion;

    protected NuxeoClient(Builder builder) {
        super(builder);
        // converter factory
        converterFactory = builder.converterFactory;
        // nuxeo cache
        nuxeoCache = builder.cache;
        // define user agent
        header(HttpHeaders.USER_AGENT, computeUserAgent());
    }

    /**
     * Used by unit tests.
     */
    protected void addOkHttpInterceptor(Interceptor interceptor) {
        okhttpBuilder.addInterceptor(interceptor);
        buildRetrofit();
    }

    protected String computeUserAgent() {
        String nuxeoPart = " NuxeoJavaClient/";
        try (InputStream inputStream = getClass().getResourceAsStream("/META-INF/nuxeo-java-client.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            String nuxeoVersion = properties.getProperty("nuxeo.java.client.version");
            nuxeoPart += nuxeoVersion;
        } catch (IOException e) {
            nuxeoPart += "Unknown";
        }
        return Version.userAgent() + nuxeoPart;
    }

    /*******************************
     * Client Services *
     ******************************/

    public NuxeoConverterFactory getConverterFactory() {
        return converterFactory;
    }

    public NuxeoResponseCache getNuxeoCache() {
        return nuxeoCache;
    }

    public boolean isCacheEnabled() {
        return nuxeoCache != null;
    }

    public NuxeoClient refreshCache() {
        if (isCacheEnabled()) {
            nuxeoCache.invalidateAll();
        }
        return this;
    }

    public void disconnect() {
        okhttpBuilder.interceptors().clear();
        headerInterceptors.clear();
        headerValues.clear();
        nuxeoCache = null;
        buildRetrofit();
    }

    /******************************
     * Services *
     ******************************/

    /**
     * This method returns the current logged user retrieved on {@link NuxeoClient} creation.
     *
     * @return the current logged user.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * This method gets the Nuxeo server version from CMIS the first time and then caches it.
     *
     * @return The Nuxeo server version.
     */
    public NuxeoVersion getServerVersion() {
        if (serverVersion == null) {
            try {
                // Remove API_PATH from the base url
                // Get repository capabilities on CMIS
                Response response = get(
                        retrofit.baseUrl().toString().replaceFirst(ConstantsV1.API_PATH, "") + "/json/cmis");
                String body = response.body().string();
                Matcher matcher = CMIS_PRODUCT_VERSION_PATTERN.matcher(body);
                if (matcher.find()) {
                    String version = matcher.group(1);
                    serverVersion = NuxeoVersion.parse(version);
                } else {
                    throw new NuxeoClientException("Unable to get version from CMIS");
                }
            } catch (IOException ioe) {
                throw new NuxeoClientException("Unable to retrieve the server version.", ioe);
            }
        }
        return serverVersion;
    }

    /**
     * @return A repository service linked to `default` repository in Nuxeo.
     */
    public Repository repository() {
        return new Repository(this);
    }

    public Repository repository(String repositoryName) {
        return new Repository(this, repositoryName);
    }

    public Operation operation(String operationId) {
        return new Operation(this, operationId);
    }

    public UserManager userManager() {
        return new UserManager(this);
    }

    public DirectoryManager directoryManager() {
        return new DirectoryManager(this);
    }

    public TaskManager taskManager() {
        return new TaskManager(this);
    }

    public BatchUploadManager batchUploadManager() {
        return new BatchUploadManager(this);
    }

    public ConfigManager configManager() {
        return new ConfigManager(this);
    }

    /*******************************
     * HTTP Services *
     ******************************/

    public Response get(String url) {
        return request(url, Request.Builder::get);
    }

    public Response delete(String url) {
        return request(url, Request.Builder::delete);
    }

    public Response delete(String url, String json) {
        RequestBody body = RequestBody.create(MediaTypes.APPLICATION_JSON_CHARSET_UTF_8.toOkHttpMediaType(), json);
        return request(url, builder -> builder.delete(body));
    }

    public Response put(String url, String json) {
        RequestBody body = RequestBody.create(MediaTypes.APPLICATION_JSON_CHARSET_UTF_8.toOkHttpMediaType(), json);
        return request(url, builder -> builder.put(body));
    }

    public Response post(String url, String json) {
        RequestBody body = RequestBody.create(MediaTypes.APPLICATION_JSON_CHARSET_UTF_8.toOkHttpMediaType(), json);
        return request(url, builder -> builder.post(body));
    }

    protected Response request(String url, UnaryOperator<Request.Builder> method) {
        try {
            Request.Builder requestBuilder = new Request.Builder().url(url);
            Request request = method.apply(requestBuilder).build();
            return retrofit.callFactory().newCall(request).execute();
        } catch (IOException e) {
            throw new NuxeoClientException("Error during call on url=" + url, e);
        }
    }

    public <T> T fetchResponse(Call<T> call) {
        if (useCache(call)) {
            String cacheKey = computeCacheKey(call);
            T result = nuxeoCache.getBody(cacheKey);
            if (result != null) {
                return result;
            }
        }
        try {
            retrofit2.Response<T> response = call.execute();
            response = handleResponse(call, response);
            return response.body();
        } catch (IOException reason) {
            throw new NuxeoClientException("Error during call on Nuxeo server", reason);
        }
    }

    public <T> void fetchResponse(Call<T> call, Callback<T> callback) {
        call.enqueue(new Callback<T>() {

            @Override
            public void onResponse(Call<T> call, retrofit2.Response<T> response) {
                try {
                    callback.onResponse(call, handleResponse(call, response));
                } catch (NuxeoClientException nce) {
                    callback.onFailure(call, nce);
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callback.onFailure(call, t);
            }

        });
    }

    @SuppressWarnings("unchecked")
    protected <T> retrofit2.Response<T> handleResponse(Call<T> call, retrofit2.Response<T> response) {
        try {
            // For redirect 308 -> the response should be success
            int httpCode = response.code();
            String httpMessage = response.message();
            if (!response.isSuccessful() && httpCode != 308) {
                // error body is not null as it's an error
                String errorBody = response.errorBody().string();
                // content type could be null
                MediaType mediaType = MediaType.fromOkHttpMediaType(response.raw().body().contentType());
                if (!StringUtils.EMPTY.equals(errorBody)
                        && MediaTypes.APPLICATION_JSON.equalsTypeSubTypeWithoutSuffix(mediaType)) {
                    throw converterFactory.readJSON(errorBody, NuxeoClientRemoteException.class);
                }
                throw new NuxeoClientRemoteException(httpCode, httpMessage, errorBody, null);
            }
            if (useCache(call)) {
                nuxeoCache.put(computeCacheKey(call), response);
            }
            T body = response.body();
            Headers headers = response.headers();
            if (body instanceof ResponseBody) {
                throw new NuxeoClientException("Internal client error, everything should be mapped to a type");
            } else if (body == null) {
                if (httpCode == 204 && MediaTypes.APPLICATION_NUXEO_EMPTY_LIST_S.equals(headers.get("Content-Type"))) {
                    return retrofit2.Response.success((T) new Blobs(), response.raw());
                }
            } else if (body instanceof Connectable) {
                ((Connectable) body).reconnectWith(this);
            } else if (body instanceof List<?>) {
                for (Object item : (List<?>) body) {
                    if (item instanceof Connectable) {
                        ((Connectable) item).reconnectWith(this);
                    }
                }
            }
            if (body instanceof Blob) {
                Blob blob = (Blob) body;

                String filename = null;
                String contentDisposition = headers.get("Content-Disposition");
                if (contentDisposition != null) {
                    filename = decodeFilename(contentDisposition);
                }
                if (filename == null) {
                    filename = blob.getFilename();
                }

                String mimeType = headers.get(HttpHeaders.CONTENT_TYPE);
                if (mimeType == null) {
                    mimeType = MediaTypes.APPLICATION_OCTET_STREAM_S;
                }
                String lengthString = headers.get(HttpHeaders.CONTENT_LENGTH);
                long length = -1;
                if (lengthString != null) {
                    length = Long.parseLong(lengthString);
                }
                if (blob instanceof StreamBlob) {
                    blob = new StreamBlob(blob.getStream(), filename, mimeType, length);
                }
                // for backward compatibility
                else if (blob instanceof FileStreamBlob) {
                    blob = new FileStreamBlob(blob.getStream(), filename, mimeType, length);
                }
                // deprecated since 3.1
                else if (blob instanceof FileBlob) {
                    blob = new FileBlob(((FileBlob) blob).getFile(), filename, mimeType);
                }
                return retrofit2.Response.success((T) blob, response.raw());
            }
            // No need to wrap the response
            return response;
        } catch (IOException ioe) {
            throw new NuxeoClientException("Error during deserialization of HTTP response", ioe);
        }
    }

    protected String decodeFilename(String contentDisposition) {
        String filename = contentDisposition.replaceFirst(".*filename\\*?=(UTF-8'')?(.*)", "$2");
        try {
            filename = URLDecoder.decode(filename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // May not happen
        }
        return filename;
    }

    protected boolean useCache(Call<?> call) {
        return isCacheEnabled() && "GET".equals(call.request().method());
    }

    /**
     * Compute the cache key with request
     */
    protected String computeCacheKey(Call<?> call) {
        Request originalRequest = call.request();
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(ConstantsV1.MD_5);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        digest.update((originalRequest.toString() + originalRequest.headers().toString()).getBytes());
        byte[] messageDigest = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte msg : messageDigest) {
            hexString.append(String.format("%02X", msg));
        }
        return hexString.toString();
    }

    /**
     * {@link NuxeoClient} builder.
     *
     * @since 3.0
     */
    public static class Builder extends AbstractBase<Builder> {

        protected final NuxeoConverterFactory converterFactory;

        protected Interceptor authenticationMethod;

        protected NuxeoResponseCache cache;

        public Builder() {
            super();
            // converter factory
            converterFactory = NuxeoConverterFactory.create();
            retrofitBuilder.addConverterFactory(converterFactory);
            // init default values
            registerEntity(EntityTypes.ANNOTATION, Annotation.class);
            registerEntity(EntityTypes.ANNOTATIONS, Annotations.class);
            registerEntity(EntityTypes.COMMENT, Comment.class);
            registerEntity(EntityTypes.COMMENTS, Comments.class);
            registerEntity(EntityTypes.DOCUMENT, Document.class);
            registerEntity(EntityTypes.DOCUMENTS, Documents.class);
            registerEntity(EntityTypes.RECORDSET, RecordSet.class);
            registerEntity(EntityTypes.TASK, Task.class);
            registerEntity(EntityTypes.TASKS, Tasks.class);
            registerEntity(EntityTypes.USER, User.class);
        }

        public Builder url(String url) {
            retrofitBuilder.baseUrl(url + ConstantsV1.API_PATH);
            return this;
        }

        public Builder authentication(String username, String password) {
            return authentication(new BasicAuthInterceptor(username, password));
        }

        public Builder authentication(Interceptor authenticationMethod) {
            this.authenticationMethod = authenticationMethod;
            return this;
        }

        public Builder interceptor(Interceptor interceptor) {
            okhttpBuilder.addInterceptor(interceptor);
            return this;
        }

        public Builder cache(NuxeoResponseCache cache) {
            this.cache = cache;
            return this;
        }

        /**
         * Register entity type to class link for automatic unmarshalling process in operation.
         * <p />
         * CAUTION: this method is just a convenient way to register entity pojo. This operation is done in a static way
         * on NuxeoConverterFactory.
         */
        public Builder registerEntity(String entityType, Class<?> clazz) {
            NuxeoConverterFactory.registerEntity(entityType, clazz);
            return this;
        }

        /**
         * Builds a {@link NuxeoClient}.
         *
         * @since 3.6
         */
        public NuxeoClient build() {
            // check authentication
            if (authenticationMethod == null) {
                throw new NuxeoClientException("Your client need an authentication method to connect to Nuxeo server");
            }
            okhttpBuilder.interceptors().add(0, authenticationMethod);
            // init client
            return new NuxeoClient(this);
        }

        /**
         * Builds a {@link NuxeoClient} and log it, it will throw a {@link NuxeoClientException} if failed.
         */
        public NuxeoClient connect() {
            NuxeoClient client = build();
            // login client on server
            client.currentUser = client.userManager().fetchCurrentUser();
            return client;
        }

    }

}
