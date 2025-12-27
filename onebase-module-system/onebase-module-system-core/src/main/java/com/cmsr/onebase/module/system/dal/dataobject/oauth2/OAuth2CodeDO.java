package com.cmsr.onebase.module.system.dal.dataobject.oauth2;

import java.time.LocalDateTime;
import java.util.List;

import com.cmsr.onebase.framework.common.enums.UserTypeEnum;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;
import lombok.Data;

/**
 * OAuth2 授权码 DO
 *
 */
@Table(value = "system_oauth2_code")
@Data
public class OAuth2CodeDO extends BaseEntity {

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
    @Column(value = CODE)
    private String code;
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
     * 客户端编号
     *
     * 关联 {@link OAuth2ClientDO#getClientId()}
     */
    @Column(value = CLIENT_ID)
    private String clientId;
    /**
     * 授权范围
     */
    @Column(value = SCOPES)
    private List<String> scopes;
    /**
     * 重定向地址
     */
    @Column(value = REDIRECT_URI)
    private String redirectUri;
    /**
     * 状态
     */
    @Column(value = STATE)
    private String state;
    /**
     * 过期时间
     */
    @Column(value = EXPIRES_TIME)
    private LocalDateTime expiresTime;

}
