package com.cmsr.onebase.module.system.dal.dataobject.license;

import lombok.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cmsr.onebase.framework.mybatis.core.dataobject.BaseDO;

/**
 * 平台License信息
 */
@TableName("system_license")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseDO extends BaseDO{
    /** 主键 */
    @TableId
    private Long id;
    /** 企业名称 */
    private String enterpriseName;
    /** 企业编号 */
    private String enterpriseCode;
    /** 企业地址 */
    private String enterpriseAddress;
    /** 平台类型 */
    private String platformType;
    /** 租户数量限制 */
    private Integer tenantLimit;
    /** 用户数量限制 */
    private Integer userLimit;
    /** 到期时间 */
    private LocalDateTime expireTime;
    /** 状态：active, expired, invalid */
    private String status;
    /** 是否为试用License */
    private Boolean isTrial;
    /** License文件 */
    private String licenseFile;
}
