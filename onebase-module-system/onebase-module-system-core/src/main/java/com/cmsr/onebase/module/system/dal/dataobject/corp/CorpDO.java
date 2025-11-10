// 8. 创建 corp 模块的 DO 对象
package com.cmsr.onebase.module.system.dal.dataobject.corp;

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
@Table(name = "system_corp")
public class CorpDO extends BaseDO {
    public static final String CORP_NAME           = "corp_name";
    public static final String CORP_CODE           = "corp_code";
    public static final String INDUSTRY_TYPE       = "industry_type";
    public static final String STATUS              = "status";
    public static final String ADDRESS             = "address";
    public static final String ADMIN_ID            = "admin_id";
    public static final String USER_LIMIT          = "user_limit";
    public static final String TENANT_ID           = "tenant_id";

    @Column(name = "corp_name")
    private String corpName;

    @Column(name = "corp_code")
    private String corpCode;

    @Column(name = "industry_type")
    private Long industryType;

    @Column(name = "status")
    private Integer status;

    @Column(name = "address")
    private String address;

    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "user_limit")
    private Integer userLimit;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "corp_logo")
    private String corpLogo;


}
