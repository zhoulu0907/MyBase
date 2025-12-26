package com.cmsr.onebase.plugin.runtime.test.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 插件 HTTP 测试工具类
 * <p>
 * 封装 HTTP 请求和响应断言，提供便捷的测试方法。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-25
 */
public class PluginHttpTestUtil {

    private static final Logger log = LoggerFactory.getLogger(PluginHttpTestUtil.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public PluginHttpTestUtil(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public PluginHttpTestUtil(RestTemplate restTemplate, String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * HTTP 响应包装类
     */
    public static class HttpResponse {
        private final int statusCode;
        private final String body;
        private final Map<String, Object> jsonBody;
        private final Exception exception;

        public HttpResponse(int statusCode, String body, Map<String, Object> jsonBody, Exception exception) {
            this.statusCode = statusCode;
            this.body = body;
            this.jsonBody = jsonBody;
            this.exception = exception;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getBody() {
            return body;
        }

        public Map<String, Object> getJsonBody() {
            return jsonBody;
        }

        public Exception getException() {
            return exception;
        }

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        public boolean isClientError() {
            return statusCode >= 400 && statusCode < 500;
        }

        public boolean isServerError() {
            return statusCode >= 500;
        }

        /**
         * 断言状态码
         */
        public HttpResponse assertStatusCode(int expectedCode) {
            assertThat(statusCode).isEqualTo(expectedCode);
            return this;
        }

        /**
         * 断言成功响应
         */
        public HttpResponse assertSuccess() {
            assertThat(isSuccess()).isTrue();
            return this;
        }

        /**
         * 断言 JSON 字段存在
         */
        public HttpResponse assertJsonFieldExists(String fieldName) {
            assertThat(jsonBody).containsKey(fieldName);
            return this;
        }

        /**
         * 断言 JSON 字段值
         */
        public HttpResponse assertJsonFieldEquals(String fieldName, Object expectedValue) {
            assertThat(jsonBody).containsEntry(fieldName, expectedValue);
            return this;
        }

        /**
         * 获取 JSON 字段值
         */
        @SuppressWarnings("unchecked")
        public <T> T getJsonField(String fieldName) {
            return (T) jsonBody.get(fieldName);
        }
    }

    /**
     * 发送 GET 请求
     */
    public HttpResponse get(String path) {
        return get(path, null);
    }

    /**
     * 发送 GET 请求（带查询参数）
     */
    public HttpResponse get(String path, Map<String, String> queryParams) {
        String url = buildUrl(path, queryParams);
        log.debug("GET {}", url);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return parseResponse(response);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return handleHttpException(e);
        } catch (Exception e) {
            log.error("GET request failed: {}", url, e);
            return new HttpResponse(0, null, null, e);
        }
    }

    /**
     * 发送 POST 请求
     */
    public HttpResponse post(String path, Object requestBody) {
        String url = buildUrl(path, null);
        log.debug("POST {} with body: {}", url, requestBody);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return parseResponse(response);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return handleHttpException(e);
        } catch (Exception e) {
            log.error("POST request failed: {}", url, e);
            return new HttpResponse(0, null, null, e);
        }
    }

    /**
     * 发送 POST 请求（JSON 字符串）
     */
    public HttpResponse postJson(String path, String jsonBody) {
        String url = buildUrl(path, null);
        log.debug("POST {} with JSON: {}", url, jsonBody);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return parseResponse(response);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return handleHttpException(e);
        } catch (Exception e) {
            log.error("POST request failed: {}", url, e);
            return new HttpResponse(0, null, null, e);
        }
    }

    /**
     * 构建完整 URL
     */
    private String buildUrl(String path, Map<String, String> queryParams) {
        StringBuilder url = new StringBuilder(baseUrl);
        if (!path.startsWith("/")) {
            url.append("/");
        }
        url.append(path);

        if (queryParams != null && !queryParams.isEmpty()) {
            url.append("?");
            queryParams.forEach((key, value) -> url.append(key).append("=").append(value).append("&"));
            // 移除最后的 &
            url.setLength(url.length() - 1);
        }

        return url.toString();
    }

    /**
     * 解析响应
     */
    @SuppressWarnings("unchecked")
    private HttpResponse parseResponse(ResponseEntity<String> response) {
        int statusCode = response.getStatusCode().value();
        String body = response.getBody();
        Map<String, Object> jsonBody = null;

        if (body != null && !body.isEmpty()) {
            try {
                jsonBody = objectMapper.readValue(body, Map.class);
            } catch (Exception e) {
                log.warn("Failed to parse JSON response: {}", body);
            }
        }

        return new HttpResponse(statusCode, body, jsonBody, null);
    }

    /**
     * 处理 HTTP 异常
     */
    @SuppressWarnings("unchecked")
    private HttpResponse handleHttpException(Exception e) {
        int statusCode = 0;
        String body = null;
        Map<String, Object> jsonBody = null;

        if (e instanceof HttpClientErrorException) {
            HttpClientErrorException clientError = (HttpClientErrorException) e;
            statusCode = clientError.getStatusCode().value();
            body = clientError.getResponseBodyAsString();
        } else if (e instanceof HttpServerErrorException) {
            HttpServerErrorException serverError = (HttpServerErrorException) e;
            statusCode = serverError.getStatusCode().value();
            body = serverError.getResponseBodyAsString();
        }

        if (body != null && !body.isEmpty()) {
            try {
                jsonBody = objectMapper.readValue(body, Map.class);
            } catch (Exception ex) {
                log.warn("Failed to parse error response: {}", body);
            }
        }

        return new HttpResponse(statusCode, body, jsonBody, e);
    }
}
