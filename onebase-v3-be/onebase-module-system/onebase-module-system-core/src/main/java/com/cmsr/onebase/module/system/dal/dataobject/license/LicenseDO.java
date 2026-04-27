package com.cmsr.onebase.module.system.dal.dataobject.license;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 平台License信息
 */
@Table(value = "system_license")
@Data
public class LicenseDO extends BaseEntity {

    public static final String ENTERPRISE_NAME    = "enterprise_name";
    public static final String ENTERPRISE_CODE    = "enterprise_code";
    public static final String ENTERPRISE_ADDRESS = "enterprise_address";
    public static final String PLATFORM_TYPE      = "platform_type";
    public static final String TENANT_LIMIT       = "tenant_limit";
    public static final String USER_LIMIT         = "user_limit";
    public static final String EXPIRE_TIME        = "expire_time";
    public static final String STATUS             = "status";
    public static final String IS_TRIAL           = "is_trial";
    public static final String LICENSE_FILE       = "license_file";
    public static final String CREATE_TIME        = "create_time";

    /** 企业名称 */
    @Column(value = ENTERPRISE_NAME)
    private String enterpriseName;
    /** 企业编号 */
    @Column(value = ENTERPRISE_CODE)
    private String enterpriseCode;
    /** 企业地址 */
    @Column(value = ENTERPRISE_ADDRESS)
    private String enterpriseAddress;
    /** 平台类型 */
    @Column(value = PLATFORM_TYPE)
    private String platformType;
    /** 租户数量限制 */
    @Column(value = TENANT_LIMIT)
    private Integer tenantLimit;
    /** 用户数量限制 */
    @Column(value = USER_LIMIT)
    private Integer userLimit;
    /** 到期时间 */
    @Column(value = EXPIRE_TIME)
    private LocalDateTime expireTime;
    /** 状态：active, expired, invalid */
    @Column(value = STATUS)
    private String status;
    /** 是否为试用License */
    @Column(value = IS_TRIAL)
    private Integer isTrial;
    /** License文件 */
    @Column(value = LICENSE_FILE)
    private String licenseFile;
    /** 创建时间 */
    @Column(value = CREATE_TIME)
    private LocalDateTime createTime;

}
