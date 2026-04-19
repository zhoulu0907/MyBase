package com.cmsr.onebase.module.system.vo.auth;

import lombok.Data;

/**
 * 灵畿平台租户信息 VO
 * 用于解析租户信息查询接口的响应数据
 */
@Data
public class LingjiTenantDetailVO {

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 租户编码
     */
    private String tenantCode;

    /**
     * 状态（1：正常；2：停用）
     */
    private Integer status;

    /**
     * 租户创建人的副账号
     */
    private String deputyAccountNumber;

    /**
     * 租户创建人的邮箱
     */
    private String email;

    /**
     * 租户创建人的姓名
     */
    private String name;

    /**
     * 所属单位类型
     * 1.集团公司 2.省公司 3.专业公司 4.直属单位
     */
    private Integer unitType;
}