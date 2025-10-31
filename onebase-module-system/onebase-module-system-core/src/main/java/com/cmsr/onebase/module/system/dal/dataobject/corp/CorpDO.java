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

    @Column(name = "corp_name")
    private String corpName;

    @Column(name = "corp_id")
    private String corpId;

    @Column(name = "industry_type")
    private Integer industryType;

    @Column(name = "status")
    private Integer status;

    @Column(name = "address")
    private String address;

    @Column(name = "admin_id")
    private String adminId;

    @Column(name = "user_limit")
    private Integer userLimit;

    @Column(name = "tenant_id")
    private Long tenantId;


}
