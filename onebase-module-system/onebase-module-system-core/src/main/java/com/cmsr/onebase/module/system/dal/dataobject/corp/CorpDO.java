// 8. 创建 corp 模块的 DO 对象
package com.cmsr.onebase.module.system.dal.dataobject.corp;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.annotation.Column;
import lombok.Data;

/**
 * 企业数据对象
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Data
@Table(value = "system_corp")
public class CorpDO extends BaseTenantEntity {
    public static final String CORP_NAME           = "corp_name";
    public static final String CORP_CODE           = "corp_code";
    public static final String INDUSTRY_TYPE       = "industry_type";
    public static final String STATUS              = "status";
    public static final String ADDRESS             = "address";
    public static final String ADMIN_ID            = "admin_id";
    public static final String USER_LIMIT          = "user_limit";
    public static final String TENANT_ID           = "tenant_id";

    @Column(value = "corp_name")
    private String corpName;

    @Column(value = "corp_code")
    private String corpCode;

    @Column(value = "industry_type")
    private Long industryType;

    @Column(value = "status")
    private Integer status;

    @Column(value = "address")
    private String address;

    @Column(value = "admin_id")
    private Long adminId;

    @Column(value = "user_limit")
    private Integer userLimit;

    @Column(value = "tenant_id")
    private Long tenantId;

    @Column(value = "corp_logo")
    private String corpLogo;


}
