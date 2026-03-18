package com.cmsr.onebase.module.system.service.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 灵畿平台 SSO 配置属性
 *
 * 配置项前缀：onebase.lingji-sso
 *
 * YAML 配置示例：
 * <pre>
 * onebase:
 *   lingji-sso:
 *     enabled: true
 *     source-id: "4298157360"
 *     source-key: "k8F5rT2zY9qX3wP7"
 *     tenant-website: "http://onebase.4c-uat.hq.cmcc:20011/appbuilder/"
 *     user-info-url: "http://rdcloud.4c-uat.hq.cmcc/moss/web/auth/v1/user/oauth/userInfo"
 *     http-debug-log-enabled: true
 *
 * # ========== 环境地址参考 ==========
 * # UAT1 承载网：http://rdcloud.4c-uat.hq.cmcc/moss/web/auth/v1/user/oauth/userInfo
 * # UAT1 内网：  http://10.102.166.153:31159/moss/web/auth/v1/user/oauth/userInfo
 * # UAT3 承载网：http://4c-uat3.hq.cmcc/moss/web/auth/v1/user/oauth/userInfo
 * # UAT3 内网：  http://10.102.166.153:31160/moss/web/auth/v1/user/oauth/userInfo
 * </pre>
 */
@ConfigurationProperties(prefix = "onebase.lingji-sso")
@RefreshScope
@Data
public class LingjiSsoProperties {

    /**
     * 是否启用灵畿 SSO
     */
    private boolean enabled = false;

    /**
     * 系统代码（灵畿平台分配）
     */
    private String sourceId;

    /**
     * 系统密钥（灵畿平台分配，用于签名）
     */
    private String sourceKey;

    /**
     * 获取用户信息接口地址
     *
     * 环境地址参考：
     * UAT1 承载网：http://rdcloud.4c-uat.hq.cmcc/moss/web/auth/v1/user/oauth/userInfo
     * UAT1 内网：  http://10.102.166.153:31159/moss/web/auth/v1/user/oauth/userInfo
     * UAT3 承载网：http://4c-uat3.hq.cmcc/moss/web/auth/v1/user/oauth/userInfo
     * UAT3 内网：  http://10.102.166.153:31160/moss/web/auth/v1/user/oauth/userInfo
     */
    private String userInfoUrl;

    /**
     * SSO 自动创建租户时使用的空间域名
     */
    private String tenantWebsite;

    /**
     * HTTP 调试日志开关（默认开启）
     */
    private boolean httpDebugLogEnabled = true;

}