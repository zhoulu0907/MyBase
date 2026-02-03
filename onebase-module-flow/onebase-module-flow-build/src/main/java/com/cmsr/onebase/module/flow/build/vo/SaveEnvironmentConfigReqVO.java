package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 保存环境配置请求 VO
 *
 * @author kanten
 * @since 2026-02-03
 */
@Data
@Schema(description = "保存环境配置请求")
public class SaveEnvironmentConfigReqVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "环境配置对象，包含 envMode 和 envConfig", required = true)
    @NotNull(message = "环境配置不能为空")
    @Valid
    private JsonNode config;
}
