package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 调试HTTP连接器动作请求 VO
 *
 * @author onebase
 * @since 2026-02-05
 */
@Data
@Schema(description = "调试HTTP连接器动作请求")
public class DebugHttpActionReqVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "请求URL（完整路径）", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://api.example.com/users")
    @NotBlank(message = "URL不能为空")
    private String url;

    @Schema(description = "请求方法（GET、POST、PUT、DELETE等）", requiredMode = Schema.RequiredMode.REQUIRED, example = "GET")
    @NotBlank(message = "请求方法不能为空")
    private String method;

    @Schema(description = "请求头列表", example = "[{\"key\":\"Authorization\",\"fieldValue\":\"Bearer token\"}]")
    private List<HttpParamFieldVO> requestHeaders;

    @Schema(description = "查询参数列表", example = "[{\"key\":\"page\",\"fieldValue\":\"1\"}]")
    private List<HttpParamFieldVO> queryParams;

    @Schema(description = "路径参数列表", example = "[{\"key\":\"userId\",\"fieldValue\":\"123\"}]")
    private List<HttpParamFieldVO> pathParams;

    @Schema(description = "请求体列表（POST/PUT请求使用）", example = "[{\"key\":\"name\",\"fieldValue\":\"test\"}]")
    private List<HttpParamFieldVO> requestBody;
}
