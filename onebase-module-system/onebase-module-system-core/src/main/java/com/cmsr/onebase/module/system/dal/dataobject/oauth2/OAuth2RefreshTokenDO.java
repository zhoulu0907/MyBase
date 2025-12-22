package com.cmsr.onebase.module.system.dal.dataobject.oauth2;

import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OAuth2 刷新令牌
 *
 */
@Table(value = "system_oauth2_refresh_token")
@Data
@Accessors(chain = true)
public class OAuth2RefreshTokenDO extends TenantBaseDO {

    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String USER_ID       = "user_id";
    public static final String USER_TYPE     = "user_type";
    public static final String CLIENT_ID     = "client_id";
    public static final String SCOPES        = "scopes";
    public static final String EXPIRES_TIME  = "expires_time";
    public static final String CORP_ID       = "corp_id";
    public static final String APP_ID        = "app_id";
    public static final String RUN_MODE      = "run_mode";

    /**
     * 刷新令牌
     */
    @Column(value = REFRESH_TOKEN)
    private String        refreshToken;
    /**
     * 用户编号
     */
    @Column(value = USER_ID)
    private Long          userId;
    /**
     * 用户类型
     * <p>
     * 枚举 {@link UserTypeEnum}
     */
    @Column(value = USER_TYPE)
    private Integer       userType;
    /**
     * 客户端编号
     * <p>
     * 关联 {@link OAuth2ClientDO#getId()}
     */
    @Column(value = CLIENT_ID)
    private String        clientId;
    /**
     * 授权范围
     */
    @Column(value = SCOPES)
    private List<String>  scopes;
    /**
     * 过期时间
     */
    @Column(value = EXPIRES_TIME)
    private LocalDateTime expiresTime;

    /**
     * 企业ID
     */
    @Column(value = CORP_ID)
    private Long corpId;

    /**
     * AppID
     */
    @Column(value = APP_ID)
    private Long   appId;
    /**
     * 运行模式 see {@link com.cmsr.onebase.framework.common.enums.RunModeEnum}
     */
    @Column(value = RUN_MODE)
    private String runMode;
}
