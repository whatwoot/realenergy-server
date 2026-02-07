package com.cs.oksdk.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.HttpUrl.Builder;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.cs.sp.common.WebAssert.throwBizException;


@Slf4j
public class OkxHttpHelper {

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    private static final OkHttpClient DEFAULT_CLIENT = createDefaultClient();
    public static final String JSON_START = "{";
    public static final String JSON_END = "}";
    public static final Integer ERROR_MSG_MAX_LENGTH = 200;

    private final OkHttpClient client;
    private final JSONWriter.Feature[] jsonFeatures = {
            JSONWriter.Feature.WriteMapNullValue,
            JSONWriter.Feature.WriteEnumsUsingName
    };

    // ----------------------- 构造函数 -----------------------

    public OkxHttpHelper() {
        this(DEFAULT_CLIENT);
    }

    public OkxHttpHelper(OkHttpClient client) {
        this.client = client;
    }

    // ======================== 同步请求方法 ========================

    public String get(String url) {
        return get(url, null, null);
    }

    public String get(String url, Object params) {
        return get(url, params, null);
    }

    public String get(String url, Object params, Map<String, String> headers) {
        return execute(buildGetRequest(url, params, headers));
    }

    public String postForm(String url) {
        return postForm(url, null, null);
    }

    public String postForm(String url, Object formData) {
        return postForm(url, formData, null);
    }

    public String postForm(String url, Object formData, Map<String, String> headers) {
        return execute(buildFormPostRequest(url, formData, headers));
    }

    public String postJson(String url, Object jsonBody) {
        return postJson(url, jsonBody, null);
    }

    public String postJson(String url, Object jsonBody, Map<String, String> headers) {
        return execute(buildJsonPostRequest(url, jsonBody, headers));
    }

    // ======================== 异步请求方法 ========================

    public CompletableFuture<String> getAsync(String url) {
        return getAsync(url, null, null);
    }

    public CompletableFuture<String> getAsync(String url, Object params) {
        return getAsync(url, params, null);
    }

    public CompletableFuture<String> getAsync(String url, Object params, Map<String, String> headers) {
        return executeAsync(buildGetRequest(url, params, headers));
    }

    public CompletableFuture<String> postFormAsync(String url, Object formData) {
        return postFormAsync(url, formData, null);
    }

    public CompletableFuture<String> postFormAsync(String url, Object formData, Map<String, String> headers) {
        return executeAsync(buildFormPostRequest(url, formData, headers));
    }

    public CompletableFuture<String> postJsonAsync(String url, Object jsonBody) {
        return postJsonAsync(url, jsonBody, null);
    }

    public CompletableFuture<String> postJsonAsync(String url, Object jsonBody, Map<String, String> headers) {
        return executeAsync(buildJsonPostRequest(url, jsonBody, headers));
    }

    // ======================== 核心构建方法 ========================

    private Request buildGetRequest(String url, Object params, Map<String, String> headers) {
        Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        Map<String, Object> paramMap = convertToMap(params);

        if (paramMap != null && !paramMap.isEmpty()) {
            paramMap.forEach((k, v) -> {
                if (isValidParam(k, v)) {
                    urlBuilder.addQueryParameter(k, v.toString());
                }
            });
        }

        Request.Builder builder = new Request.Builder()
                .url(urlBuilder.build())
                .get();

        addHeaders(builder, headers);
        return builder.build();
    }

    private Request buildFormPostRequest(String url, Object formData, Map<String, String> headers) {
        FormBody formBody = buildFormBody(formData);
        RequestBody requestBody = formBody != null ? formBody : new FormBody.Builder().build();

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);

        addHeaders(builder, headers);
        return builder.build();
    }

    private Request buildJsonPostRequest(String url, Object jsonBody, Map<String, String> headers) {
        String jsonContent = parseJsonContent(jsonBody);
        RequestBody body = RequestBody.create(jsonContent, JSON_TYPE);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body)
                .header(CONTENT_TYPE, APPLICATION_JSON);

        addHeaders(builder, headers);
        return builder.build();
    }

    // ======================== 工具方法 ========================

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object obj) {
        if (obj == null) return Collections.emptyMap();

        if (obj instanceof JSONObject) {
            return (JSONObject) obj;
        } else if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        } else {
            return JSONObject.from(obj);
        }
    }

    private FormBody buildFormBody(Object formData) {
        Map<String, Object> formMap = convertToMap(formData);
        if (formMap == null || formMap.isEmpty()) return null;

        FormBody.Builder builder = new FormBody.Builder();
        formMap.forEach((k, v) -> {
            if (isValidParam(k, v)) {
                builder.add(k, v.toString());
            }
        });
        return builder.build();
    }

    private String parseJsonContent(Object jsonBody) {
        if (jsonBody == null) return "";
        if (jsonBody instanceof String) return (String) jsonBody;
        if (jsonBody instanceof JSONObject) return ((JSONObject) jsonBody).toJSONString();
        return JSON.toJSONString(jsonBody, jsonFeatures);
    }

    private boolean isValidParam(String key, Object value) {
        return StringUtils.isNotBlank(key) && value != null;
    }

    private void addHeaders(Request.Builder builder, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            headers.forEach((k, v) -> {
                if (isValidParam(k, v)) {
                    builder.addHeader(k, v);
                }
            });
        }
    }

    // ======================== 执行方法 ========================

    private String execute(Request request) {
        String requestId = RandomStringUtils.randomAlphanumeric(8);
        request = request.newBuilder().tag(String.class, requestId).build();
        logRequestStart(requestId, request);
        Response response = null;
        try {
            response = client.newCall(request).execute();
            return handleResponse(response);
        } catch (IOException e) {
            log.error("[{}] Http-fail {} | URL: {} | Error: {}",
                    requestId,
                    response != null ? response.code() : "",
                    request.url(),
                    e.getMessage());
            throwBizException(e.getMessage());
        }
        return null;
    }

    private CompletableFuture<String> executeAsync(Request request) {
        String requestId = RandomStringUtils.randomAlphanumeric(8);
        request = request.newBuilder().tag(String.class, requestId).build();
        logRequestStart(requestId, request);

        CompletableFuture<String> future = new CompletableFuture<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    future.complete(handleResponse(response));
                } catch (IOException e) {
                    log.error("[{}] Http-async fail", requestId, e);
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                log.error("[{}] Http-async fail", requestId, e);
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    private String handleResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            String errorBody;
            if (response.body() != null) {
                errorBody = response.body().string();
                if(!(errorBody.startsWith(JSON_START) && errorBody.endsWith(JSON_END))) {
                    JSONObject json = new JSONObject();
                    json.put("code", response.code());
                    json.put("msg", StringUtils.truncate(errorBody, ERROR_MSG_MAX_LENGTH));
                    errorBody = json.toJSONString();
                }
            } else {
                // TODO: 特殊场景，因为结果都是返回该值
                JSONObject json = new JSONObject();
                json.put("code", response.code());
                json.put("msg", response.message());
                errorBody = json.toJSONString();
            }
            throw new IOException(errorBody);
        }
        return response.body().string();
    }

    // ======================== 日志方法 ========================

    private void logRequestStart(String requestId, Request request) {
        log.debug("[{}] >> {} {}", requestId, request.method(), request.url());

        if (request.body() instanceof FormBody) {
            FormBody formBody = (FormBody) request.body();
            log.debug("[{}] FormParams: {}", requestId, formBodyToString(formBody));
        } else if (request.body() instanceof RequestBody) {
            log.debug("[{}] ContentType: {}", requestId, request.body().contentType());
        }
    }

    private String formBodyToString(FormBody body) {
        if (body == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < body.size(); i++) {
            sb.append(body.name(i)).append("=").append(body.value(i));
            if (i < body.size() - 1) sb.append("&");
        }
        return sb.toString();
    }

    // ======================== 客户端配置 ========================

    private static OkHttpClient createDefaultClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(Runtime.getRuntime().availableProcessors(), 1, TimeUnit.MINUTES))
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor())
                .build();
    }

    public static OkHttpClient createSuddenClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(Runtime.getRuntime().availableProcessors(), 30, TimeUnit.SECONDS))
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor())
                .build();
    }

    /**
     * 增强日志拦截器
     */
    public static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String requestId = request.tag(String.class);
            long startNs = System.nanoTime();
            // 请求前日志
            log.debug("[{}] Http-req: {} {}", requestId, request.headers(), request.body());
            Response response = chain.proceed(request);
            // 请求后日志
            long costMs = (System.nanoTime() - startNs) / 1_000_000;
            log.debug("[{}] Http-res: {} {} ({}ms)",
                    requestId, response.code(), response.message(), costMs);
            return response;
        }
    }
}