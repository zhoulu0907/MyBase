package com.cmsr.onebase.module.infra.dal.dataflexdo.logger;

import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.module.infra.enums.logger.ApiErrorLogProcessStatusEnum;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * API 异常数据
 *
 */
@Data
@TenantIgnore
@NoArgsConstructor
@AllArgsConstructor
@Table("infra_api_error_log")
public class ApiErrorLogDO extends BaseTenantEntity {
    // builder模式可正常运作
    public ApiErrorLogDO setId(Long id){
        super.setId(id);
        return this;
    }
    /**
     * {@link #requestParams} 的最大长度
     */
    public static final Integer REQUEST_PARAMS_MAX_LENGTH = 8000;

    // 字段常量定义
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_TRACE_ID = "trace_id";
    public static final String COLUMN_USER_TYPE = "user_type";
    public static final String COLUMN_APPLICATION_NAME = "application_name";
    public static final String COLUMN_REQUEST_METHOD = "request_method";
    public static final String COLUMN_REQUEST_URL = "request_url";
    public static final String COLUMN_REQUEST_PARAMS = "request_params";
    public static final String COLUMN_USER_IP = "user_ip";
    public static final String COLUMN_USER_AGENT = "user_agent";
    public static final String COLUMN_EXCEPTION_TIME = "exception_time";
    public static final String COLUMN_EXCEPTION_NAME = "exception_name";
    public static final String COLUMN_EXCEPTION_MESSAGE = "exception_message";
    public static final String COLUMN_EXCEPTION_ROOT_CAUSE_MESSAGE = "exception_root_cause_message";
    public static final String COLUMN_EXCEPTION_STACK_TRACE = "exception_stack_trace";
    public static final String COLUMN_EXCEPTION_CLASS_NAME = "exception_class_name";
    public static final String COLUMN_EXCEPTION_FILE_NAME = "exception_file_name";
    public static final String COLUMN_EXCEPTION_METHOD_NAME = "exception_method_name";
    public static final String COLUMN_EXCEPTION_LINE_NUMBER = "exception_line_number";
    public static final String COLUMN_PROCESS_STATUS = "process_status";
    public static final String COLUMN_PROCESS_TIME = "process_time";
    public static final String COLUMN_PROCESS_USER_ID = "process_user_id";

    /**
     * 用户编号
     */
    @Column(value = COLUMN_USER_ID)
    private Long userId;
    /**
     * 链路追踪编号
     *
     * 一般来说，通过链路追踪编号，可以将访问日志，错误日志，链路追踪日志，logger 打印日志等，结合在一起，从而进行排错。
     */
    @Column(value = COLUMN_TRACE_ID)
    private String traceId;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    @Column(value = COLUMN_USER_TYPE)
    private Integer userType;
    /**
     * 应用名
     *
     * 目前读取 spring.application.name
     */
    @Column(value = COLUMN_APPLICATION_NAME)
    private String applicationName;

    // ========== 请求相关字段 ==========

    /**
     * 请求方法名
     */
    @Column(value = COLUMN_REQUEST_METHOD)
    private String requestMethod;
    /**
     * 访问地址
     */
    @Column(value = COLUMN_REQUEST_URL)
    private String requestUrl;
    /**
     * 请求参数
     *
     * query: Query String
     * body: Quest Body
     */
    @Column(value = COLUMN_REQUEST_PARAMS)
    private String requestParams;
    /**
     * 用户 IP
     */
    @Column(value = COLUMN_USER_IP)
    private String userIp;
    /**
     * 浏览器 UA
     */
    @Column(value = COLUMN_USER_AGENT)
    private String userAgent;

    // ========== 异常相关字段 ==========

    /**
     * 异常发生时间
     */
    @Column(value = COLUMN_EXCEPTION_TIME)
    private LocalDateTime exceptionTime;
    /**
     * 异常名
     *
     * {@link Throwable#getClass()} 的类全名
     */
    @Column(value = COLUMN_EXCEPTION_NAME)
    private String exceptionName;
    /**
     * 异常导致的消息
     *
     *
     */
    @Column(value = COLUMN_EXCEPTION_MESSAGE)
    private String exceptionMessage;
    /**
     * 异常导致的根消息
     *
     *
     */
    @Column(value = COLUMN_EXCEPTION_ROOT_CAUSE_MESSAGE)
    private String exceptionRootCauseMessage;
    /**
     * 异常的栈轨迹
     *
     * {@link org.apache.commons.lang3.exception.ExceptionUtils#getStackTrace(Throwable)}
     */
    @Column(value = COLUMN_EXCEPTION_STACK_TRACE)
    private String exceptionStackTrace;
    /**
     * 异常发生的类全名
     *
     * {@link StackTraceElement#getClassName()}
     */
    @Column(value = COLUMN_EXCEPTION_CLASS_NAME)
    private String exceptionClassName;
    /**
     * 异常发生的类文件
     *
     * {@link StackTraceElement#getFileName()}
     */
    @Column(value = COLUMN_EXCEPTION_FILE_NAME)
    private String exceptionFileName;
    /**
     * 异常发生的方法名
     *
     * {@link StackTraceElement#getMethodName()}
     */
    @Column(value = COLUMN_EXCEPTION_METHOD_NAME)
    private String exceptionMethodName;
    /**
     * 异常发生的方法所在行
     *
     * {@link StackTraceElement#getLineNumber()}
     */
    @Column(value = COLUMN_EXCEPTION_LINE_NUMBER)
    private Integer exceptionLineNumber;

    // ========== 处理相关字段 ==========

    /**
     * 处理状态
     *
     * 枚举 {@link ApiErrorLogProcessStatusEnum}
     */
    @Column(value = COLUMN_PROCESS_STATUS)
    private Integer processStatus;
    /**
     * 处理时间
     */
    @Column(value = COLUMN_PROCESS_TIME)
    private LocalDateTime processTime;
    /**
     * 处理用户编号
     *
     * 关联 com.cmsr.onebase.adminserver.modules.system.dal.dataobject.user.SysUserDO.SysUserDO#getId()
     */
    @Column(value = COLUMN_PROCESS_USER_ID)
    private Long processUserId;

}