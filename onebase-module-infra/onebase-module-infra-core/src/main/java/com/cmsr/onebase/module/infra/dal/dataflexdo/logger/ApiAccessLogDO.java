package com.cmsr.onebase.module.infra.dal.dataflexdo.logger;

import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * API 访问日志
 */
@Data
@TenantIgnore
@NoArgsConstructor
@AllArgsConstructor
@Table("infra_api_access_log")
public class ApiAccessLogDO extends BaseTenantEntity {

    /**
     * {@link #requestParams} 的最大长度
     */
    public static final Integer REQUEST_PARAMS_MAX_LENGTH = 8000;

    /**
     * {@link #resultMsg} 的最大长度
     */
    public static final Integer RESULT_MSG_MAX_LENGTH = 512;

    // 字段常量定义
    public static final String COLUMN_TRACE_ID = "trace_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_TYPE = "user_type";
    public static final String COLUMN_APPLICATION_NAME = "application_name";
    public static final String COLUMN_REQUEST_METHOD = "request_method";
    public static final String COLUMN_REQUEST_URL = "request_url";
    public static final String COLUMN_REQUEST_PARAMS = "request_params";
    public static final String COLUMN_RESPONSE_BODY = "response_body";
    public static final String COLUMN_USER_IP = "user_ip";
    public static final String COLUMN_USER_AGENT = "user_agent";
    public static final String COLUMN_OPERATE_MODULE = "operate_module";
    public static final String COLUMN_OPERATE_NAME = "operate_name";
    public static final String COLUMN_OPERATE_TYPE = "operate_type";
    public static final String COLUMN_BEGIN_TIME = "begin_time";
    public static final String COLUMN_END_TIME = "end_time";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_RESULT_CODE = "result_code";
    public static final String COLUMN_RESULT_MSG = "result_msg";

    /**
     * 链路追踪编号
     * <p>
     * 一般来说，通过链路追踪编号，可以将访问日志，错误日志，链路追踪日志，logger 打印日志等，结合在一起，从而进行排错。
     */
    @Column(value = COLUMN_TRACE_ID)
    private String traceId;
    /**
     * 用户编号
     */
    @Column(value = COLUMN_USER_ID)
    private Long userId;
    /**
     * 用户类型
     * <p>
     * 枚举 {@link UserTypeEnum}
     */
    @Column(value = COLUMN_USER_TYPE)
    private Integer userType;
    /**
     * 应用名
     * <p>
     * 目前读取 `spring.application.name` 配置项
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
     * <p>
     * query: Query String
     * body: Quest Body
     */
    @Column(value = COLUMN_REQUEST_PARAMS)
    private String requestParams;
    /**
     * 响应结果
     */
    @Column(value = COLUMN_RESPONSE_BODY)
    private String responseBody;
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

    // ========== 执行相关字段 ==========

    /**
     * 操作模块
     */
    @Column(value = COLUMN_OPERATE_MODULE)
    private String operateModule;
    /**
     * 操作名
     */
    @Column(value = COLUMN_OPERATE_NAME)
    private String operateName;
    /**
     * 操作分类
     * <p>
     */
    @Column(value = COLUMN_OPERATE_TYPE)
    private Integer operateType;

    /**
     * 开始请求时间
     */
    @Column(value = COLUMN_BEGIN_TIME)
    private LocalDateTime beginTime;
    /**
     * 结束请求时间
     */
    @Column(value = COLUMN_END_TIME)
    private LocalDateTime endTime;
    /**
     * 执行时长，单位：毫秒
     */
    @Column(value = COLUMN_DURATION)
    private Integer duration;

    /**
     * 结果码
     * <p>
     * 目前使用的 {@link CommonResult#getCode()} 属性
     */
    @Column(value = COLUMN_RESULT_CODE)
    private Integer resultCode;
    /**
     * 结果提示
     * <p>
     * 目前使用的 {@link CommonResult#getMsg()} 属性
     */
    @Column(value = COLUMN_RESULT_MSG)
    private String resultMsg;

}