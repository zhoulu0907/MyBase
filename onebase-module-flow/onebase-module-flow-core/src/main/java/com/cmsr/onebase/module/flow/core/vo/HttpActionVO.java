package com.cmsr.onebase.module.flow.core.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * HTTP动作响应VO
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Schema(description = "HTTP动作响应VO")
@Data
public class HttpActionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 应用ID
     */
    @Schema(description = "应用ID")
    private Long applicationId;

    /**
     * 所属连接器UUID
     */
    @Schema(description = "所属连接器UUID")
    private String connectorUuid;

    /**
     * HTTP动作UUID
     */
    @Schema(description = "HTTP动作UUID")
    private String httpUuid;

    /**
     * HTTP动作名称
     */
    @Schema(description = "HTTP动作名称")
    private String httpName;

    /**
     * HTTP动作编码
     */
    @Schema(description = "HTTP动作编码")
    private String httpCode;

    /**
     * 动作描述
     */
    @Schema(description = "动作描述")
    private String description;

    /**
     * HTTP请求方法
     */
    @Schema(description = "HTTP请求方法")
    private String requestMethod;

    /**
     * 请求路径
     */
    @Schema(description = "请求路径")
    private String requestPath;

    /**
     * Query参数
     */
    @Schema(description = "Query参数")
    private List<Object> requestQuery;

    /**
     * 请求头
     */
    @Schema(description = "请求头")
    private List<Object> requestHeaders;

    /**
     * 请求体类型
     */
    @Schema(description = "请求体类型")
    private String requestBodyType;

    /**
     * 请求体模板
     */
    @Schema(description = "请求体模板")
    private String requestBodyTemplate;

    /**
     * 认证方式
     */
    @Schema(description = "认证方式")
    private String authType;

    /**
     * 认证配置
     */
    @Schema(description = "认证配置")
    private Object authConfig;

    /**
     * 响应映射
     */
    @Schema(description = "响应映射")
    private Object responseMapping;

    /**
     * 成功条件
     */
    @Schema(description = "成功条件")
    private Object successCondition;

    /**
     * 输入Schema
     */
    @Schema(description = "输入Schema")
    private List<Object> inputSchema;

    /**
     * 输出Schema
     */
    @Schema(description = "输出Schema")
    private List<Object> outputSchema;

    /**
     * 超时时间
     */
    @Schema(description = "超时时间")
    private Integer timeout;

    /**
     * 重试次数
     */
    @Schema(description = "重试次数")
    private Integer retryCount;

    /**
     * Mock响应
     */
    @Schema(description = "Mock响应")
    private String mockResponse;

    /**
     * 启用状态
     */
    @Schema(description = "启用状态")
    private Integer activeStatus;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;
}
