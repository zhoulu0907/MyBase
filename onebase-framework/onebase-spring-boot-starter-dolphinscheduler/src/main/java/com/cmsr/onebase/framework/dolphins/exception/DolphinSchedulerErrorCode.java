package com.cmsr.onebase.framework.dolphins.exception;

/**
 * DolphinScheduler 错误码枚举
 *
 * @author matianyu
 * @date 2025-01-17
 */
public enum DolphinSchedulerErrorCode {

    // ==================== 通用错误 ====================
    /** 未知错误 */
    UNKNOWN_ERROR("DS_UNKNOWN", "未知错误"),
    /** 网络异常 */
    NETWORK_ERROR("DS_NETWORK", "网络异常"),
    /** 请求超时 */
    TIMEOUT_ERROR("DS_TIMEOUT", "请求超时"),

    // ==================== 认证错误 ====================
    /** 认证失败 */
    AUTH_ERROR("DS_AUTH", "认证失败"),
    /** Token 无效 */
    TOKEN_INVALID("DS_TOKEN_INVALID", "Token 无效或已过期"),
    /** 权限不足 */
    PERMISSION_DENIED("DS_PERMISSION_DENIED", "权限不足"),

    // ==================== 业务错误 ====================
    /** 工作流不存在 */
    WORKFLOW_NOT_FOUND("DS_WORKFLOW_NOT_FOUND", "工作流不存在"),
    /** 项目不存在 */
    PROJECT_NOT_FOUND("DS_PROJECT_NOT_FOUND", "项目不存在"),
    /** 任务不存在 */
    TASK_NOT_FOUND("DS_TASK_NOT_FOUND", "任务不存在"),
    /** 定时任务不存在 */
    SCHEDULE_NOT_FOUND("DS_SCHEDULE_NOT_FOUND", "定时任务不存在"),
    /** 队列不存在 */
    QUEUE_NOT_FOUND("DS_QUEUE_NOT_FOUND", "队列不存在"),
    /** 工作流实例不存在 */
    WORKFLOW_INSTANCE_NOT_FOUND("DS_WORKFLOW_INSTANCE_NOT_FOUND", "工作流实例不存在"),
    /** 任务实例不存在 */
    TASK_INSTANCE_NOT_FOUND("DS_TASK_INSTANCE_NOT_FOUND", "任务实例不存在"),

    // ==================== API 调用错误 ====================
    /** API 调用失败 */
    API_CALL_FAILED("DS_API_FAILED", "API 调用失败"),
    /** 参数校验失败 */
    PARAM_INVALID("DS_PARAM_INVALID", "参数校验失败"),
    /** 资源已存在 */
    RESOURCE_ALREADY_EXISTS("DS_RESOURCE_EXISTS", "资源已存在"),
    /** 操作不允许 */
    OPERATION_NOT_ALLOWED("DS_OPERATION_NOT_ALLOWED", "操作不允许"),

    // ==================== 响应处理错误 ====================
    /** 响应解析失败 */
    RESPONSE_PARSE_ERROR("DS_PARSE_ERROR", "响应解析失败"),
    /** 响应状态异常 */
    RESPONSE_STATUS_ERROR("DS_STATUS_ERROR", "响应状态异常"),
    /** 响应数据为空 */
    RESPONSE_DATA_EMPTY("DS_DATA_EMPTY", "响应数据为空");

    private final String code;
    private final String message;

    DolphinSchedulerErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
