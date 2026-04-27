package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 保存连接器动作配置请求 VO
 *
 * @author kanten
 * @since 2026-02-04
 */
@Data
@Schema(description = "保存连接器动作配置请求")
public class SaveActionConfigReqVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "动作配置（包含 basic, requestHeaders, requestBody, queryParams, pathParams）",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "{\"basic\":{\"actionName\":\"hahaha1\",\"description\":\"描述\"},\"requestHeaders\":[...]}")
    @NotNull(message = "动作配置不能为空")
    @Valid
    private JsonNode actionConfig;
}
