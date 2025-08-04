package com.cmsr.onebase.module.system.dal.dataobject.oauth2;

import java.time.LocalDateTime;
import java.util.List;

import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * OAuth2 授权码 DO
 *
 */
@Table(name = "system_oauth2_code")
@Data
public class OAuth2CodeDO extends BaseDO {

    public static final String CODE         = "code";
    public static final String USER_ID      = "user_id";
    public static final String USER_TYPE    = "user_type";
    public static final String CLIENT_ID    = "client_id";
    public static final String SCOPES       = "scopes";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String STATE        = "state";
    public static final String EXPIRES_TIME = "expires_time";

    /**
     * 授权码
     */
    @Column(name = CODE)
    private String code;
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
     * 客户端编号
     *
     * 关联 {@link OAuth2ClientDO#getClientId()}
     */
    @Column(name = CLIENT_ID)
    private String clientId;
    /**
     * 授权范围
     */
    @Column(name = SCOPES)
    private List<String> scopes;
    /**
     * 重定向地址
     */
    @Column(name = REDIRECT_URI)
    private String redirectUri;
    /**
     * 状态
     */
    @Column(name = STATE)
    private String state;
    /**
     * 过期时间
     */
    @Column(name = EXPIRES_TIME)
    private LocalDateTime expiresTime;

}
