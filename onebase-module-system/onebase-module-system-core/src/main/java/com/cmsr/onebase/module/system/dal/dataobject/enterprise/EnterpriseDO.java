// 8. 创建 enterprise 模块的 DO 对象
package com.cmsr.onebase.module.system.dal.dataobject.enterprise;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 企业数据对象
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Data
@Table(name = "system_enterprise")
public class EnterpriseDO extends BaseDO {

    @Column(name = "enterprise_name")
    private String enterpriseName;

    @Column(name = "enterprise_code")
    private String enterpriseCode;

    @Column(name = "industry_type")
    private Integer industryType;

    @Column(name = "status")
    private Integer status;

    @Column(name = "address")
    private String address;

    @Column(name = "admin_id")
    private String adminId;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "authorized_apps")
    private String authorizedApps;

    @Column(name = "user_count")
    private Integer userCount;


    @Column(name = "tenant_id")
    private Integer tenantId;

    @Column(name = "deleted")
    private Long deleted;
}
