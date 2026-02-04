package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 环境配置 Schema VO
 * <p>
 * 用于返回指定环境的动作配置 Formily Schema
 *
 * @author kanten
 * @since 2026-01-30
 */
@Data
@Schema(description = "环境配置 Schema（Formily）")
public class EnvironmentConfigVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "动作的 Formily Schema（包含 type、title、properties 等完整配置）")
    private JsonNode schema;

    @Schema(description = "环境编码")
    private String envCode;

    @Schema(description = "连接器类型编号")
    private String typeCode;
}
