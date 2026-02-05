package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Schema(description = "执行HTTP连接器动作响应VO")
@Data
@Builder
public class ExecuteHttpActionRespVO {

    @Schema(description = "执行状态：success/fail")
    private String status;

    @Schema(description = "执行时间（毫秒）")
    private Long duration;

    @Schema(description = "HTTP状态码")
    private Integer statusCode;

    @Schema(description = "响应头")
    private Map<String, List<String>> headers;

    @Schema(description = "响应体（解析后的对象）")
    private Object body;

    @Schema(description = "原始响应体（字符串）")
    private String rawBody;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "请求详情")
    private RequestDetail requestDetail;

    @Schema(description = "执行结果消息")
    private String message;

    @Data
    @Builder
    public static class RequestDetail {
        @Schema(description = "请求URL")
        private String url;

        @Schema(description = "请求方法")
        private String method;

        @Schema(description = "请求头")
        private Map<String, String> headers;

        @Schema(description = "请求体")
        private String body;
    }
}
