package com.cmsr.onebase.module.system.dal.dataobject.logger;

import jakarta.persistence.Table;
import jakarta.persistence.Column;

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
@Table(name = "system_login_log")
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
    @Column(name = LOG_TYPE)
    private Integer logType;
    /**
     * 链路追踪编号
     */
    @Column(name = TRACE_ID)
    private String traceId;
    /**
     * 用户编号
     */
    @Column(name = USER_ID)
    private Long userId;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    @Column(name = USER_TYPE)
    private Integer userType;
    /**
     * 用户账号
     *
     * 冗余，因为账号可以变更
     */
    @Column(name = USERNAME)
    private String username;
    /**
     * 登录结果
     *
     * 枚举 {@link LoginResultEnum}
     */
    @Column(name = RESULT)
    private Integer result;
    /**
     * 用户 IP
     */
    @Column(name = USER_IP)
    private String userIp;
    /**
     * 浏览器 UA
     */
    @Column(name = USER_AGENT)
    private String userAgent;

}
