package com.cmsr.onebase.module.system.dal.dataobject.logger;

import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;

import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.module.system.enums.logger.LoginLogTypeEnum;
import com.cmsr.onebase.module.system.enums.logger.LoginResultEnum;
import lombok.Data;

/**
 * 登录日志表
 *
 * 注意，包括登录和登出两种行为
 *
 */
@Table(value = "system_login_log")
@Data
public class LoginLogDO extends TenantBaseDO {

    public static final String LOG_TYPE   = "log_type";
    public static final String TRACE_ID   = "trace_id";
    public static final String USER_ID    = "user_id";
    public static final String USER_TYPE  = "user_type";
    public static final String USERNAME   = "username";
    public static final String RESULT     = "result";
    public static final String USER_IP    = "user_ip";
    public static final String USER_AGENT = "user_agent";

    /**
     * 日志类型
     *
     * 枚举 {@link LoginLogTypeEnum}
     */
    @Column(value = LOG_TYPE)
    private Integer logType;
    /**
     * 链路追踪编号
     */
    @Column(value = TRACE_ID)
    private String traceId;
    /**
     * 用户编号
     */
    @Column(value = USER_ID)
    private Long userId;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    @Column(value = USER_TYPE)
    private Integer userType;
    /**
     * 用户账号
     *
     * 冗余，因为账号可以变更
     */
    @Column(value = USERNAME)
    private String username;
    /**
     * 登录结果
     *
     * 枚举 {@link LoginResultEnum}
     */
    @Column(value = RESULT)
    private Integer result;
    /**
     * 用户 IP
     */
    @Column(value = USER_IP)
    private String userIp;
    /**
     * 浏览器 UA
     */
    @Column(value = USER_AGENT)
    private String userAgent;

}
