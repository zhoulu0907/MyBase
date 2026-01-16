package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * HTTP连接器动作配置数据对象
 *
 * <p>对应表: flow_connector_http
 * 存储HTTP请求的具体配置，与flow_connector表关联
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "flow_connector_http")
public class FlowConnectorHttpDO extends BaseAppEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属连接器UUID
     * 关联到 flow_connector.connector_uuid
     */
    @Column(value = "connector_uuid")
    private String connectorUuid;

    /**
     * HTTP动作UUID
     * 全局唯一标识，用于流程定义中引用
     */
    @Column(value = "http_uuid")
    private String httpUuid;

    /**
     * HTTP动作名称
     * 如: "获取用户信息"、"创建订单"
     */
    @Column(value = "http_name")
    private String httpName;

    /**
     * HTTP动作编码
     * 如: "GET_USER_INFO"、"CREATE_ORDER"
     */
    @Column(value = "http_code")
    private String httpCode;

    /**
     * 动作描述
     */
    @Column(value = "description")
    private String description;

    /**
     * HTTP请求方法
     * GET/POST/PUT/PATCH/DELETE
     */
    @Column(value = "request_method")
    private String requestMethod;

    /**
     * 请求路径
     * 支持变量替换，如: /api/v1/users/${userId}
     */
    @Column(value = "request_path")
    private String requestPath;

    /**
     * Query参数定义
     * JSON格式: List<HttpParameter>
     */
    @Column(value = "request_query")
    private String requestQuery;

    /**
     * 请求头定义
     * JSON格式: List<HttpHeader>
     */
    @Column(value = "request_headers")
    private String requestHeaders;

    /**
     * 请求体类型
     * JSON/FORM/RAW/NONE
     */
    @Column(value = "request_body_type")
    private String requestBodyType;

    /**
     * 请求体模板
     * 支持变量替换
     */
    @Column(value = "request_body_template")
    private String requestBodyTemplate;

    /**
     * 认证方式
     * NONE/BASIC/TOKEN/OAUTH2/CUSTOM_SIGNATURE/INHERIT
     */
    @Column(value = "auth_type")
    private String authType;

    /**
     * 认证配置
     * JSON格式
     */
    @Column(value = "auth_config")
    private String authConfig;

    /**
     * 响应字段映射定义
     * JSON格式
     */
    @Column(value = "response_mapping")
    private String responseMapping;

    /**
     * 成功条件表达式
     * JSON格式
     */
    @Column(value = "success_condition")
    private String successCondition;

    /**
     * 输入参数Schema
     * JSON格式，供前端生成表单
     */
    @Column(value = "input_schema")
    private String inputSchema;

    /**
     * 输出参数Schema
     * JSON格式
     */
    @Column(value = "output_schema")
    private String outputSchema;

    /**
     * 超时时间（毫秒）
     * 覆盖连接器配置
     */
    @Column(value = "timeout")
    private Integer timeout;

    /**
     * 重试次数
     * 覆盖连接器配置
     */
    @Column(value = "retry_count")
    private Integer retryCount;

    /**
     * Mock响应
     * 用于测试
     */
    @Column(value = "mock_response")
    private String mockResponse;

    /**
     * 启用状态
     * 0-禁用, 1-启用
     */
    @Column(value = "active_status")
    private Integer activeStatus;

    /**
     * 排序
     */
    @Column(value = "sort_order")
    private Integer sortOrder;
}
