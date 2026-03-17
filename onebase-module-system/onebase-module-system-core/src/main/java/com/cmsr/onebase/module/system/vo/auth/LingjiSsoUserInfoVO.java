package com.cmsr.onebase.module.system.vo.auth;

import lombok.Data;

/**
 * 灵畿平台 SSO 用户信息 VO
 * 解析 JWT id_token 中的用户信息
 */
@Data
public class LingjiSsoUserInfoVO {

    /**
     * 系统代码
     */
    private String sourceid;

    /**
     * 用户手机号
     */
    private String sub;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 企业ID
     */
    private String enterpriseId;

    /**
     * 企业编码
     */
    private String enterpriseCode;

    /**
     * 企业用户ID
     */
    private String euserId;

    /**
     * 外部企业ID
     */
    private String externalEnterpriseid;

    /**
     * 副账号
     */
    private String deputyAccountNumber;

    /**
     * 主账号（工号）
     */
    private String staffCode;

    /**
     * 签发者
     */
    private String iss;

    /**
     * 过期时间
     */
    private String exp;

    /**
     * 签发时间
     */
    private String iat;

    /**
     * 内部人员标识（1-内部，0-外部）
     */
    private Integer insider;
}