package com.cmsr.onebase.module.system.dal.dataobject.oauth2;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;
import lombok.Data;

/**
 * OAuth2 外部客户端配置 DO
 *
 */
@Data
@TenantIgnore
@Table(value = "system_oauth2_client_out_config")
public class OAuth2ClientOutConfigDO extends BaseEntity {

    // 字段常量
    public static final String TENANT_CODE = "tenant_code";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String ACCESS_TOKEN_URL = "access_token_url";
    public static final String USER_INFO_URL = "user_info_url";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String SCOPE = "scope";
    public static final String HTTP_DEBUG_LOG_ENABLED = "http_debug_log_enabled";
    public static final String STATUS = "status";
    public static final String REMARK = "remark";
    // 四共 SSO 扩展字段
    public static final String SOURCE_ID = "source_id";
    public static final String SOURCE_KEY = "source_key";
    public static final String AUTHORIZE_URL = "authorize_url";
    public static final String ENTERPRISE_MAPPING = "enterprise_mapping";

    /**
     * 租户编码
     */
    @Column(value = TENANT_CODE)
    private String tenantCode;

    /**
     * 客户端编号
     */
    @Column(value = CLIENT_ID)
    private String clientId;

    /**
     * 客户端密钥
     */
    @Column(value = CLIENT_SECRET)
    private String clientSecret;

    /**
     * 获取访问令牌地址
     */
    @Column(value = ACCESS_TOKEN_URL)
    private String accessTokenUrl;

    /**
     * 获取用户信息地址
     */
    @Column(value = USER_INFO_URL)
    private String userInfoUrl;

    /**
     * 重定向地址
     */
    @Column(value = REDIRECT_URI)
    private String redirectUri;

    /**
     * 授权范围
     */
    @Column(value = SCOPE)
    private String scope;

    /**
     * HTTP 调试日志开关（0-关闭，1-开启）
     */
    @Column(value = HTTP_DEBUG_LOG_ENABLED)
    private Boolean httpDebugLogEnabled;

    /**
     * 状态
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(value = STATUS)
    private Integer status;

    /**
     * 备注
     */
    @Column(value = REMARK)
    private String remark;

    // ========== 四共 SSO 扩展字段 ==========

    /**
     * 系统代码（四共平台分配）
     */
    @Column(value = SOURCE_ID)
    private String sourceId;

    /**
     * 系统密钥（四共平台分配，用于签名）
     */
    @Column(value = SOURCE_KEY)
    private String sourceKey;

    /**
     * 授权码页面URL（前端跳转用）
     */
    @Column(value = AUTHORIZE_URL)
    private String authorizeUrl;

    /**
     * 企业ID到租户的映射规则（JSON格式）
     * 示例：{"xxjs": 1, "tenant001": 2, "default": 1}
     */
    @Column(value = ENTERPRISE_MAPPING)
    private String enterpriseMapping;

}
