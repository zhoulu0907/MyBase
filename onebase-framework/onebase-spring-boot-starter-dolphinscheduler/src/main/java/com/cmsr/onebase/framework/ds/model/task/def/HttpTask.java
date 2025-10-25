package com.cmsr.onebase.framework.ds.model.task.def;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class HttpTask extends AbstractTask {

    private String url;

    private String httpMethod;

    private String httpBody = "";

    private List<HttpParam> httpParams = new ArrayList<>();

    private String httpCheckCondition = "STATUS_CODE_DEFAULT"; // STATUS_CODE_DEFAULT

    private String condition = "";

    private Integer connectTimeout = 60000;

    private Integer socketTimeout = 60000;

    public static HttpTask ofUrl(String url) {
        HttpTask httpTask = new HttpTask();
        httpTask.setUrl(url);

        return httpTask;
    }

    public HttpTask method(HttpMethod method) {
        this.httpMethod = method.name().toUpperCase();
        return this;
    }

    public HttpTask header(String headerName, String value) {
        httpParams.add(HttpParam.header(headerName, value));
        return this;
    }

    public HttpTask form(String parameter, String value) {
        httpParams.add(HttpParam.form(parameter, value));
        return this;
    }

    public HttpTask body(String body) {
        this.httpBody = body;
        return this;
    }

    @Override
    public String grantTaskType() {
        return "HTTP";
    }

    @Getter
    @Setter
    public static class HttpParam {
        private String prop;
        private String value;
        private String httpParametersType;

        public HttpParam(String prop, String value, String httpParametersType) {
            this.prop = prop;
            this.value = value;
            this.httpParametersType = httpParametersType;
        }

        public static HttpParam form(String key, String value) {
            return new HttpParam(key, value, "PARAMETER");
        }

        public static HttpParam header(String key, String value) {
            return new HttpParam(key, value, "HEADERS");
        }

        @Override
        public String toString() {
            return JsonUtils.toJsonString(this);
        }
    }

    public enum HttpMethod {
        GET,
        POST,
        DELETE,
        PUT,
        PATCH,
        OPTIONS,
        TRACE,
        HEAD;
    }

}
