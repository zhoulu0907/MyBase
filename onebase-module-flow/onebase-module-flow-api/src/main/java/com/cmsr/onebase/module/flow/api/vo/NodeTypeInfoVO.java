package com.cmsr.onebase.module.flow.api.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 连接器类型详情 VO（含完整 Schema）
 * <p>
 * 用于 type-info 接口返回连接器类型的完整配置信息，
 * 包括基本信息和用于前端动态渲染表单的 JSON Schema。
 * </p>
 * <p>
 * 此 VO 数据量较大，仅用于单个类型的详情查询。
 * 列表展示请使用轻量级的 NodeInfoVO。
 * </p>
 *
 * @author zhoulu
 * @since 2026-01-22
 */
@Schema(description = "连接器类型详情（含完整 Schema）")
@Data
public class NodeTypeInfoVO {

    /**
     * 连接器类型编码
     */
    @Schema(description = "连接器类型编码")
    private String nodeCode;

    /**
     * 连接器类型名称
     */
    @Schema(description = "连接器类型名称")
    private String nodeName;

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
     * 连接配置 Schema（Formily JSON Schema v2）
     * <p>
     * 用于前端渲染向导第 2 步的环境配置表单
     * </p>
     */
    @Schema(description = "连接配置 Schema")
    private JsonNode connConfig;

    /**
     * 动作配置 Schema（Formily JSON Schema v2）
     * <p>
     * 用于前端渲染向导第 3 步的动作配置表单
     * </p>
     */
    @Schema(description = "动作配置 Schema")
    private JsonNode actionConfig;

}
