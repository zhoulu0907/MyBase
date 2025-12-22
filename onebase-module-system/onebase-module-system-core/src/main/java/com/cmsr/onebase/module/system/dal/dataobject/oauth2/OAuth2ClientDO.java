package com.cmsr.onebase.module.system.dal.dataobject.oauth2;

import java.util.List;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.enums.oauth2.OAuth2GrantTypeEnum;

import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;
import lombok.Data;

/**
 * OAuth2 客户端 DO
 *
 */
@Data
@TenantIgnore
@Table(value = "system_oauth2_client")
public class OAuth2ClientDO extends BaseEntity {

    // 字段常量
    public static final String CLIENT_ID                     = "client_id";
    public static final String SECRET                        = "secret";
    public static final String NAME                          = "name";
    public static final String LOGO                          = "logo";
    public static final String DESCRIPTION                   = "description";
    public static final String STATUS                        = "status";
    public static final String ACCESS_TOKEN_VALIDITY_SECONDS = "access_token_validity_seconds";
    public static final String REFRESH_TOKEN_VALIDITY_SECONDS= "refresh_token_validity_seconds";
    public static final String REDIRECT_URIS                 = "redirect_uris";
    public static final String AUTHORIZED_GRANT_TYPES        = "authorized_grant_types";
    public static final String SCOPES                        = "scopes";
    public static final String AUTO_APPROVE_SCOPES           = "auto_approve_scopes";
    public static final String AUTHORITIES                   = "authorities";
    public static final String RESOURCE_IDS                  = "resource_ids";
    public static final String ADDITIONAL_INFORMATION        = "additional_information";

    /**
     * 客户端编号
     */
    @Column(value = CLIENT_ID)
    private String clientId;
    /**
     * 客户端密钥
     */
    @Column(value = SECRET)
    private String secret;
    /**
     * 应用名
     */
    @Column(value = NAME)
    private String name;
    /**
     * 应用图标
     */
    @Column(value = LOGO)
    private String logo;
    /**
     * 应用描述
     */
    @Column(value = DESCRIPTION)
    private String description;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(value = STATUS)
    private Integer status;
    /**
     * 访问令牌的有效期
     */
    @Column(value = ACCESS_TOKEN_VALIDITY_SECONDS)
    private Integer accessTokenValiditySeconds;
    /**
     * 刷新令牌的有效期
     */
    @Column(value = REFRESH_TOKEN_VALIDITY_SECONDS)
    private Integer refreshTokenValiditySeconds;
    /**
     * 可重定向的 URI 地址
     */
    @Column(value = REDIRECT_URIS)
    private List<String> redirectUris;
    /**
     * 授权类型（模式）
     *
     * 枚举 {@link OAuth2GrantTypeEnum}
     */
    @Column(value = AUTHORIZED_GRANT_TYPES)
    private List<String> authorizedGrantTypes;
    /**
     * 授权范围
     */
    @Column(value = SCOPES)
    private List<String> scopes;
    /**
     * 自动授权的 Scope
     *
     * code 授权时，如果 scope 在这个范围内，则自动通过
     */
    @Column(value = AUTO_APPROVE_SCOPES)
    private List<String> autoApproveScopes;
    /**
     * 权限
     */
    @Column(value = AUTHORITIES)
    private List<String> authorities;
    /**
     * 资源
     */
    @Column(value = RESOURCE_IDS)
    private List<String> resourceIds;
    /**
     * 附加信息，JSON 格式
     */
    @Column(value = ADDITIONAL_INFORMATION)
    private String additionalInformation;

}
