package com.cmsr.onebase.module.system.vo.platform;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import lombok.Data;

/**
 * 平台信息请求VO
 */
@Data
public class PlatformInfoReqVo extends PageParam {

    /**
     * 平台ID
     */
    private Long id;

    /**
     * 平台名称
     */
    private String name;

    /**
     * 平台描述
     */
    private String description;

    /**
     * 平台状态
     */
    private Integer status;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 企业编号
     */
    private String companyCode;

    /**
     * 企业地址
     */
    private String companyAddress;

    /**
     * 超级管理员
     */
    private String superAdmin;

    /**
     * 平台类型
     */
    private String platformType;

    /**
     * 认证状态
     */
    private Integer authStatus;

    /**
     * 到期时间
     */
    private java.time.LocalDateTime expireTime;

    /**
     * 系统版本
     */
    private String systemVersion;

    /**
     * 租户数量
     */
    private Integer tenantCount;

    /**
     * 管理员
     */
    private String adminUser;
}