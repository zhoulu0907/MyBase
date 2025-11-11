package com.cmsr.onebase.module.system.dal.dataobject.oauth2;

import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * OAuth2 访问令牌 DO
 *
 * 如下字段，暂时未使用，暂时不支持：
 * user_name、authentication（用户信息）
 *
 */
@Table(name = "system_oauth2_access_token")
@Data
@Accessors(chain = true)
public class OAuth2AccessTokenDO extends TenantBaseDO {

    public static final String ACCESS_TOKEN  = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String USER_ID       = "user_id";
    public static final String USER_TYPE     = "user_type";
    public static final String USER_INFO     = "user_info";
    public static final String CLIENT_ID     = "client_id";
    public static final String SCOPES        = "scopes";
    public static final String EXPIRES_TIME  = "expires_time";
    public static final String CORP_ID       = "app_id";
    public static final String APP_ID        = "corp_id";
    /**
     * 访问令牌
     */
    @Column(name = ACCESS_TOKEN)
    private String accessToken;
    /**
     * 刷新令牌
     */
    @Column(name = REFRESH_TOKEN)
    private String refreshToken;
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
     * 用户信息
     */
    @Column(name = USER_INFO)
    private Map<String, String> userInfo;
    /**
     * 客户端编号
     *
     * 关联 {@link OAuth2ClientDO#getId()}
     */
    @Column(name = CLIENT_ID)
    private String clientId;
    /**
     * 授权范围
     */
    @Column(name = SCOPES)
    private List<String> scopes;
    /**
     * 过期时间
     */
    @Column(name = EXPIRES_TIME)
    private LocalDateTime expiresTime;

    /**
     * 企业ID
     */
    @Column(name = CORP_ID)
    private Long corpId;

    /**
     * AppID
     */
    @Column(name = APP_ID)
    private Long appId;
}
