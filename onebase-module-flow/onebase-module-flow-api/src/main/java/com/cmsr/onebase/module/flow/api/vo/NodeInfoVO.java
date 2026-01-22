package com.cmsr.onebase.module.flow.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 连接器类型信息 VO
 */
@Data
@Schema(description = "连接器类型信息")
public class NodeInfoVO {

    /**
     * 连接器类型名称
     */
    @Schema(description = "连接器类型名称")
    private String nodeName;

    /**
     * 一级分类编码
     */
    @Schema(description = "一级分类编码")
    private String level1Code;

    /**
     * 二级分类编码
     */
    @Schema(description = "二级分类编码")
    private String level2Code;

    /**
     * 三级分类编码
     */
    @Schema(description = "三级分类编码")
    private String level3Code;

    /**
     * 版本号
     */
    @Schema(description = "版本号")
    private String version;

    /**
     * 认证方式: API_KEY, BASIC_AUTH, OAUTH2, NONE
     */
    @Schema(description = "认证方式")
    private String authType;

    /**
     * 该类型的连接器实例数量
     */
    @Schema(description = "实例数量")
    private Integer instanceCount;
}
