package com.cmsr.onebase.module.flow.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 连接器类型信息 VO（轻量版）
 * <p>
 * 用于连接器类型列表展示，仅包含基本信息，不包含大字段的 Schema 配置。
 * </p>
 * <p>
 * 如需获取完整的 Schema 配置，请使用 NodeTypeInfoVO
 * </p>
 *
 * @author zhoulu
 * @since 2026-01-22
 */
@Data
@Schema(description = "连接器类型信息（轻量版）")
public class NodeInfoVO {

    /**
     * 连接器类型名称
     */
    @Schema(description = "连接器类型名称")
    private String nodeName;

    /**
     * 连接器类型编码
     */
    @Schema(description = "连接器类型编码")
    private String nodeCode;

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
