package com.cmsr.onebase.module.system.dal.dataobject.license;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.SuperBuilder;import java.time.LocalDateTime;
import com.cmsr.onebase.framework.data.base.BaseDO;

/**
 * 平台License信息
 */
@Table(name = "system_license")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseDO extends BaseDO{

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
    @Column(name = ENTERPRISE_NAME)
    private String enterpriseName;
    /** 企业编号 */
    @Column(name = ENTERPRISE_CODE)
    private String enterpriseCode;
    /** 企业地址 */
    @Column(name = ENTERPRISE_ADDRESS)
    private String enterpriseAddress;
    /** 平台类型 */
    @Column(name = PLATFORM_TYPE)
    private String platformType;
    /** 租户数量限制 */
    @Column(name = TENANT_LIMIT)
    private Integer tenantLimit;
    /** 用户数量限制 */
    @Column(name = USER_LIMIT)
    private Integer userLimit;
    /** 到期时间 */
    @Column(name = EXPIRE_TIME)
    private LocalDateTime expireTime;
    /** 状态：active, expired, invalid */
    @Column(name = STATUS)
    private String status;
    /** 是否为试用License */
    @Column(name = IS_TRIAL)
    private Integer isTrial;
    /** License文件 */
    @Column(name = LICENSE_FILE)
    private String licenseFile;
    /** 创建时间 */
    @Column(name = CREATE_TIME)
    private LocalDateTime createTime;

}
