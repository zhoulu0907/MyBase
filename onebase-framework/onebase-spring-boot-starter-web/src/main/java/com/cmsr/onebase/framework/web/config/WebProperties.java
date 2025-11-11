package com.cmsr.onebase.framework.web.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;

@ConfigurationProperties(prefix = "onebase.web")
@RefreshScope
@Validated
@Data
public class WebProperties {

    @NotNull(message = "Runtime API 不能为空")
    private Api runtimeApi = new Api("/runtime", new String[]{"**.controller.app.**", "**.runtime.controller.**"});

    @NotNull(message = "Build API 不能为空")
    private Api buildApi = new Api("/admin-api", new String[]{
        "**.controller.admin.**",
        "**.build.controller.**",
        "**.module.**.controller.admin.**"
    });

    @NotNull(message = "Platform API 不能为空")
    private Api platformApi = new Api("/platform", new String[]{
            "**.platform.controller.**",
    });

    // @NotNull(message = "Admin UI 不能为空")
    private Ui adminUi;

    /**
     * 是否在响应中返回异常堆栈。仅建议开发/测试环境打开。
     * 配置项：onebase.web.return-exception-stack-trace
     */
    private boolean returnExceptionStackTrace = false;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Valid
    public static class Api {

        /**
         * API 前缀，实现所有 Controller 提供的 RESTFul API 的统一前缀
         * <p>
         * <p>
         * 意义：通过该前缀，避免 Swagger、Actuator 意外通过 Nginx 暴露出来给外部，带来安全性问题
         * 这样，Nginx 只需要配置转发到 /api/* 的所有接口即可。
         *
         * @see OneBaseWebAutoConfiguration#configurePathMatch(PathMatchConfigurer)
         */
        @NotEmpty(message = "API 前缀不能为空")
        private String prefix;

        /**
         * Controller 所在包的 Ant 路径规则
         * <p>
         * 主要目的是，给该 Controller 设置指定的 {@link #prefix}
         */
        @NotEmpty(message = "Controller 所在包不能为空")
        private String[] controller;

    }

    @Data
    @Valid
    public static class Ui {

        /**
         * 访问地址
         */
        private String url;

    }

}
