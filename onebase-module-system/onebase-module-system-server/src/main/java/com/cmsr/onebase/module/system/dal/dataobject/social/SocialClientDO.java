package com.cmsr.onebase.module.system.dal.dataobject.social;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.module.system.enums.social.SocialTypeEnum;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.SuperBuilder;import me.zhyd.oauth.config.AuthConfig;

/**
 * 社交客户端 DO
 *
 * 对应 {@link AuthConfig} 配置，满足不同租户，有自己的客户端配置，实现社交（三方）登录
 *
 */
@Table(name = "system_social_client")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SocialClientDO extends TenantBaseDO {

    // 列名常量
    public static final String NAME          = "name";
    public static final String SOCIAL_TYPE   = "social_type";
    public static final String USER_TYPE     = "user_type";
    public static final String STATUS        = "status";
    public static final String CLIENT_ID     = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String AGENT_ID      = "agent_id";

    /**
     * 应用名
     */
    @Column(name = NAME)
    private String name;
    /**
     * 社交类型
     *
     * 枚举 {@link SocialTypeEnum}
     */
    @Column(name = SOCIAL_TYPE)
    private Integer socialType;
    /**
     * 用户类型
     *
     * 目的：不同用户类型，对应不同的小程序，需要自己的配置
     *
     * 枚举 {@link UserTypeEnum}
     */
    @Column(name = USER_TYPE)
    private Integer userType;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    @Column(name = STATUS)
    private Integer status;

    /**
     * 客户端 id
     */
    @Column(name = CLIENT_ID)
    private String clientId;
    /**
     * 客户端 Secret
     */
    @Column(name = CLIENT_SECRET)
    private String clientSecret;

    /**
     * 代理编号
     *
     * 目前只有部分“社交类型”在使用：
     * 1. 企业微信：对应授权方的网页应用 ID
     */
    @Column(name = AGENT_ID)
    private String agentId;

}
