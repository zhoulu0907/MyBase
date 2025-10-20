package com.cmsr.onebase.framework.dolphins.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 通用参数属性 DTO
 *
 * 对应 swagger 中的 Property 定义
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class PropertyDTO {

    /** 名称 */
    @JsonProperty("prop")
    private String prop;

    /** 方向 IN/OUT */
    @JsonProperty("direct")
    private String direct;

    /** 类型 VARCHAR/INTEGER/... */
    @JsonProperty("type")
    private String type;

    /** 值（字符串） */
    @JsonProperty("value")
    private String value;
}
