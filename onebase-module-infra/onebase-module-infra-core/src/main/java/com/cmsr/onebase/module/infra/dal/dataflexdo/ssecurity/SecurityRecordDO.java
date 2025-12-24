package com.cmsr.onebase.module.infra.dal.dataflexdo.ssecurity;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 租户安全记录表
 *
 * @author matianyu
 * @date 2025-11-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("infra_security_record")
public class SecurityRecordDO extends BaseTenantEntity {

    public static final String TENANT_ID = "tenant_id";
    public static final String USER_ID = "user_id";
    public static final String RECORD_TYPE = "record_type";
    public static final String RECORD_VALUE = "record_value";

    /**
     * 租户ID
     */
    @Column(value = TENANT_ID)
    private Long tenantId;

    /**
     * 用户ID
     */
    @Column(value = USER_ID)
    private Long userId;

    /**
     * 记录类型（PASSWORD_HISTORY-密码历史）
     */
    @Column(value = RECORD_TYPE)
    private String recordType;

    /**
     * 记录值（加密后的密码）
     */
    @Column(value = RECORD_VALUE)
    private String recordValue;

}
