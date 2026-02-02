package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 环境配置模板 VO
 * <p>
 * 用于返回连接器类型的环境配置 Formily Schema 模板
 *
 * @author kanten
 * @since 2026-01-30
 */
@Data
@Schema(description = "环境配置模板（Formily Schema）")
public class EnvConfigTemplateVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "环境配置的 Formily Schema（包含完整的表单结构）")
    private JsonNode schema;
}
