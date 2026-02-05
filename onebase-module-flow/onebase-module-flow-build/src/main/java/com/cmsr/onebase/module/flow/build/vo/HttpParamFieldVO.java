package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * HTTP参数字段VO
 * <p>
 * 用于表示HTTP请求的参数字段（请求头、查询参数、路径参数、请求体等）
 * 包含完整的字段定义信息
 *
 * @author onebase
 * @since 2026-02-05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "HTTP参数字段")
public class HttpParamFieldVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "参数键名", example = "Authorization")
    private String key;

    @Schema(description = "字段名称", example = "授权信息")
    private String fieldName;

    @Schema(description = "字段类型", example = "string")
    private String fieldType;

    @Schema(description = "是否必填", example = "false")
    private Boolean required;

    @Schema(description = "默认值", example = "")
    private String defaultValue;

    @Schema(description = "参数描述", example = "用于身份验证的令牌")
    private String description;

    @Schema(description = "唯一标识", example = "row-1770196815952-e8wn06dje9k")
    private String id;

    @Schema(description = "参数值", example = "Bearer token123")
    private String fieldValue;
}
